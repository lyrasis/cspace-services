package org.collectionspace.services.listener.botgarden;

import org.collectionspace.services.batch.nuxeo.UpdateDeadFlagBatchJob;
import org.collectionspace.services.client.workflow.WorkflowClient;
import org.collectionspace.services.collectionobject.nuxeo.CollectionObjectConstants;
import org.collectionspace.services.common.ResourceMap;
import org.collectionspace.services.common.invocable.InvocationResults;
import org.collectionspace.services.common.relation.nuxeo.RelationConstants;
import org.collectionspace.services.movement.nuxeo.MovementBotGardenConstants;
import org.collectionspace.services.movement.nuxeo.MovementConstants;
import org.collectionspace.services.nuxeo.listener.AbstractCSEventListenerImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateDeadFlagListener extends AbstractCSEventListenerImpl {
	final Logger logger = LoggerFactory.getLogger(UpdateDeadFlagListener.class);

	/*
	 * Set the dead flag and dead date on collectionobjects related to a new or modified movement record.
	 */
	@Override
	public void handleEvent(Event event) {
		EventContext ec = event.getContext();

		if (isRegistered(event) && ec instanceof DocumentEventContext) {
			DocumentEventContext context = (DocumentEventContext) ec;
			DocumentModel doc = context.getSourceDocument();

			logger.debug("docType=" + doc.getType());

			if (event.getName().equals(DocumentEventTypes.DOCUMENT_CREATED)) {
				/*
				 * Handle the case where a new movement is created with action code revive, and then related
				 * to a collectionobject. The movement won't have any relations at the time it's created,
				 * so we need to capture the creation of the relation.
				 */
				if (doc.getType().equals(RelationConstants.NUXEO_DOCTYPE) &&
						!doc.isVersion() &&
						!doc.isProxy()) {
					String subjectDocType = (String) doc.getProperty(RelationConstants.SUBJECT_DOCTYPE_SCHEMA_NAME, RelationConstants.SUBJECT_DOCTYPE_FIELD_NAME);
					String objectDocType = (String) doc.getProperty(RelationConstants.OBJECT_DOCTYPE_SCHEMA_NAME, RelationConstants.OBJECT_DOCTYPE_FIELD_NAME);;

					logger.debug("subjectDocType=" + subjectDocType + " objectDocType=" + objectDocType);

					if (subjectDocType.equals(MovementConstants.NUXEO_DOCTYPE) && objectDocType.equals(CollectionObjectConstants.NUXEO_DOCTYPE)) {
						String movementCsid = (String) doc.getProperty(RelationConstants.SUBJECT_CSID_SCHEMA_NAME, RelationConstants.SUBJECT_CSID_FIELD_NAME);
						String collectionObjectCsid = (String) doc.getProperty(RelationConstants.OBJECT_CSID_SCHEMA_NAME, RelationConstants.OBJECT_CSID_FIELD_NAME);

						try {
							InvocationResults results = createUpdater().updateDeadFlag(collectionObjectCsid, movementCsid);

							logger.debug("updateDeadFlag complete: numAffected=" + results.getNumAffected() + " userNote=" + results.getUserNote());
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			}
			else {
				/*
				 * Handle document modification. If the modified document was a movement record, and
				 * its action code is dead or revived, update the dead flag. We don't actually have to
				 * check the action code here, since it will be checked inside UpdateDeadFlagBatchJob.updateRelatedDeadFlags,
				 * but it is an optimization.
				 */
				if (doc.getType().startsWith(MovementConstants.NUXEO_DOCTYPE) &&
						!doc.isVersion() &&
						!doc.isProxy() &&
						!doc.getCurrentLifeCycleState().equals(WorkflowClient.WORKFLOWSTATE_DELETED)) {
					String actionCode = (String) doc.getProperty(MovementBotGardenConstants.ACTION_CODE_SCHEMA_NAME, MovementBotGardenConstants.ACTION_CODE_FIELD_NAME);

					logger.debug("actionCode=" + actionCode);

					if (actionCode != null && (actionCode.equals(MovementBotGardenConstants.DEAD_ACTION_CODE) || actionCode.equals(MovementBotGardenConstants.REVIVED_ACTION_CODE))) {
						String movementCsid = doc.getName();

						try {
							InvocationResults results = createUpdater().updateRelatedDeadFlags(movementCsid);

							logger.debug("updateRelatedDeadFlags complete: numAffected=" + results.getNumAffected() + " userNote=" + results.getUserNote());
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			}
		}
	}

	private UpdateDeadFlagBatchJob createUpdater() {
		ResourceMap resourceMap = ResteasyProviderFactory.getContextData(ResourceMap.class);

		UpdateDeadFlagBatchJob updater = new UpdateDeadFlagBatchJob();
		updater.setResourceMap(resourceMap);

		return updater;
	}
}
