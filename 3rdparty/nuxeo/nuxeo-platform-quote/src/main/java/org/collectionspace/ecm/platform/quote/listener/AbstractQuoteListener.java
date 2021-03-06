/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.collectionspace.ecm.platform.quote.listener;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.platform.relations.api.RelationManager;
import org.nuxeo.runtime.api.Framework;

import org.collectionspace.ecm.platform.quote.service.QuoteServiceConfig;
import org.collectionspace.ecm.platform.quote.service.QuoteServiceHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractQuoteListener {

     private static final Logger logger = LoggerFactory.getLogger(AbstractQuoteListener.class);

        public void handleEvent(EventBundle events) {
            for (Event event : events) {
                handleEvent(event);
            }
        }

        public void handleEvent(Event event) {
            if (DocumentEventTypes.DOCUMENT_REMOVED.equals(event.getName())) {
                EventContext ctx = event.getContext();
                if (ctx instanceof DocumentEventContext) {
                    DocumentEventContext docCtx = (DocumentEventContext) ctx;
                    DocumentModel doc = docCtx.getSourceDocument();
                    CoreSession coreSession = docCtx.getCoreSession();
                    QuoteServiceConfig config = QuoteServiceHelper.getQuoteService().getConfig();
                    try {
                        RelationManager relationManager = Framework.getService(RelationManager.class);
                        doProcess(coreSession, relationManager, config, doc);
                    }
                    catch (Exception e) {
                        log.error("Error during message processing", e);
                    }
                    return;
                }
            }
        }

        protected abstract void doProcess(CoreSession coreSession,
            RelationManager relationManager, QuoteServiceConfig config,
            DocumentModel docMessage) throws Exception;

}
