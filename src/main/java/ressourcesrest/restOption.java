package ressourcesrest;

import entities.Option;
import metiers.OptionBusiness;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("options")
public class restOption {
    public static OptionBusiness optB = new OptionBusiness();

    // 1) Création d'une nouvelle option
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response ajoutOption(Option op) {
        if (op != null && optB.addOption(op)) {
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    // 2) Récupération de la liste de toutes les options & 3) d'un domaine spécifique
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOptions(@QueryParam("domaine") String domaine) {
        List<Option> list;
        if (domaine != null && !domaine.trim().isEmpty()) {
            list = optB.getOptionsByDomaine(domaine);
        } else {
            list = optB.getListeOptions();
        }
        return Response.status(200).entity(list).build();
    }

    // 6) Récupération d'une option ayant un code donné
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOptionByCode(@PathParam("id") int id) {
        Option op = optB.getOptionByCode(id);
        if (op != null) {
            return Response.status(200).entity(op).build();
        }
        return Response.status(404).build();
    }

    // 4) Suppression d'une option ayant un code spécifique
    @DELETE
    @Path("{id}")
    public Response deleteOption(@PathParam("id") int id) {
        if (optB.deleteOption(id)) {
            return Response.status(204).build();
        }
        return Response.status(404).build();
    }

    // 5) Modification d'une option ayant un code spécifique
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateOption(@PathParam("id") int id, Option op) {
        if (op != null && optB.updateOption(id, op)) {
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }
}
