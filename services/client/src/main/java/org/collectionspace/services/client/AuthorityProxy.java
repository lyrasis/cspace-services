package org.collectionspace.services.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.collectionspace.services.client.workflow.WorkflowClient;
import org.collectionspace.services.common.api.CommonAPI;

/*
 * ILT = Item list type
 * LT = List type
 */
public interface AuthorityProxy extends CollectionSpaceCommonListPoxProxy {
	
	/*
	 * Basic CRUD operations
	 */
	
    //(C)reate Item
    @POST
    @Path("/{vcsid}/items/")
    Response createItem(@PathParam("vcsid") String vcsid, byte[] xmlPayload);

    //(R)ead Item
    @GET
    @Path("/{vcsid}/items/{csid}")
    Response readItem(@PathParam("vcsid") String vcsid,
    		@PathParam("csid") String csid,
    		@QueryParam(WorkflowClient.WORKFLOWSTATE_QUERY) String workflowState,
    		@QueryParam(CommonAPI.showRelations_QP) String showRelations);
    
    
    //(U)pdate Item
    @PUT
    @Path("/{vcsid}/items/{csid}")
    Response updateItem(@PathParam("vcsid") String vcsid, @PathParam("csid") String csid, byte[] xmlPayload);
    
    //(U)pdate Item    
    @PUT
    @Path("/urn:cspace:name({specifier})/items/urn:cspace:name({itemspecifier})")
    Response updateNamedItemInNamedAuthority(
    		@PathParam("specifier")String specifier,
    		@PathParam("itemspecifier")String itemspecifier, 
    		byte[] xmlPayload);

    //(D)elete Item
    @DELETE
    @Path("/{vcsid}/items/{csid}")
    Response deleteItem(@PathParam("vcsid") String vcsid, @PathParam("csid") String csid);
    
    //(D)elete Item
    @DELETE
    @Path("/urn:cspace:name({specifier})/items/urn:cspace:name({itemspecifier})")
    public Response deleteNamedItemInNamedAuthority(
    		@PathParam("specifier")String specifier,
    		@PathParam("itemspecifier")String itemspecifier);

    
    /**
     * Get a list of objects that reference a given authority term.
     * 
     * @param parentcsid 
     * @param itemcsid 
     * @param csid
     * @return
     * @see org.collectionspace.services.client.IntakeProxy#getAuthorityRefs(java.lang.String)
     */
    @GET
    @Path("{csid}/items/{itemcsid}/refObjs")
    @Produces("application/xml")
    Response getReferencingObjects( // ClientResponse<AuthorityRefDocList>
            @PathParam("csid") String parentcsid,
            @PathParam("itemcsid") String itemcsid,
            @QueryParam(WorkflowClient.WORKFLOWSTATE_QUERY) String workflowState);
    
    // List Item Authority References
    @GET
    @Produces({"application/xml"})
    @Path("/{parentcsid}/items/{itemcsid}/authorityrefs/")
    public Response getItemAuthorityRefs( // public ClientResponse<AuthorityRefList>
            @PathParam("parentcsid") String parentcsid,
            @PathParam("itemcsid") String itemcsid);
    
    /*
     * Synchronization methods
     */
    
    // Sync by name
    @POST
    @Path("/urn:cspace:name({name})/sync")
    Response syncByName(@PathParam("name") String name);
    
    // Sync by name or CSID
    @POST
    @Path("/{identifier}/sync")
    public Response sync(@PathParam("identifier") String identifier);
    
    /*
     * READ/GET Methods
     */
    
    //(R)ead by name
    @GET
    @Path("/urn:cspace:name({name})")
    Response readByName(@PathParam("name") String name,
    		@QueryParam(WorkflowClient.WORKFLOWSTATE_QUERY) String workflowState);
    
    /*
     * Item subresource methods
     */
    
    //(R)ead Named Item
    @GET
    @Path("/{vcsid}/items/urn:cspace:name({specifier})")
    Response readNamedItem(@PathParam("vcsid") String vcsid,
    		@PathParam("specifier") String specifier,
    		@QueryParam(WorkflowClient.WORKFLOWSTATE_QUERY) String workflowState);

    //(R)ead Item In Named Authority
    @GET
    @Path("/urn:cspace:name({specifier})/items/{csid}")
    Response readItemInNamedAuthority(@PathParam("specifier") String specifier,
    		@PathParam("csid") String csid,
    		@QueryParam(WorkflowClient.WORKFLOWSTATE_QUERY) String workflowState);

    //(R)ead Named Item In Named Authority
    @GET
    @Path("/urn:cspace:name({specifier})/items/urn:cspace:name({itemspecifier})")
    Response readNamedItemInNamedAuthority(@PathParam("specifier") String specifier, 
    		@PathParam("itemspecifier") String itemspecifier,
    		@QueryParam(WorkflowClient.WORKFLOWSTATE_QUERY) String workflowState,
    		@QueryParam(CommonAPI.showRelations_QP) String showRelations);
    
    /*
     * Item subresource List methods
     */

    // List Items matching a partial term or keywords.
    @GET
    @Produces({"application/xml"})
    @Path("/{csid}/items/")
    Response readItemList(
    		@PathParam("csid") String vcsid,
            @QueryParam (IQueryManager.SEARCH_TYPE_PARTIALTERM) String partialTerm,
            @QueryParam(IQueryManager.SEARCH_TYPE_KEYWORDS_KW) String keywords,
            @QueryParam(WorkflowClient.WORKFLOWSTATE_QUERY) String workflowState);
    
    @GET
    @Produces({"application/xml"})
    @Path("/{csid}/items/")
    Response readItemList(
    		@PathParam("csid") String vcsid,
            @QueryParam (IQueryManager.SEARCH_TYPE_PARTIALTERM) String partialTerm,
            @QueryParam(IQueryManager.SEARCH_TYPE_KEYWORDS_KW) String keywords,
            @QueryParam(WorkflowClient.WORKFLOWSTATE_QUERY) String workflowState,
            @QueryParam(IClientQueryParams.PAGE_SIZE_PARAM) long pageSize,
            @QueryParam(IClientQueryParams.START_PAGE_PARAM) long pageNum);
    
    // List Items for a named authority matching a partial term or keywords.
    @GET
    @Produces({"application/xml"})
    @Path("/urn:cspace:name({specifier})/items/")
    Response readItemListForNamedAuthority( // ClientResponse<AbstractCommonList>
    		@PathParam("specifier") String specifier,
            @QueryParam (IQueryManager.SEARCH_TYPE_PARTIALTERM) String partialTerm,
            @QueryParam(IQueryManager.SEARCH_TYPE_KEYWORDS_KW) String keywords,
            @QueryParam(WorkflowClient.WORKFLOWSTATE_QUERY) String workflowState);

    /*
     * Workflow related methods
     * 
     */
    
    //(R)ead Item workflow
    @GET
    @Produces({"application/xml"})
    @Consumes({"application/xml"})    
    @Path("/{vcsid}/items/{csid}" + WorkflowClient.SERVICE_PATH)
    Response readItemWorkflow(@PathParam("vcsid") String vcsid,
    		@PathParam("csid") String csid);
            
    //(U)pdate Item workflow
    @PUT
    @Path("/{vcsid}/items/{csid}" + WorkflowClient.SERVICE_PATH + "/{transition}")
    Response updateItemWorkflowWithTransition(
    		@PathParam("vcsid") String vcsid,
    		@PathParam("csid") String csid,
    		@PathParam("transition") String transition);
    
}
