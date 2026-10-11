package ressourcesrest;

import entities.Etudiant;
import entities.EtudiantList;
import entities.Option;
import metiers.EtudiantBusiness;
import metiers.OptionBusiness;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("etudiants")
public class restEtudiant {
    public static EtudiantBusiness etudiantBusiness = new EtudiantBusiness();
    public static OptionBusiness optionBusiness = new OptionBusiness();

    // 1) Création d'un nouvel étudiant
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addEtudiant(Etudiant etudiant) {
        if (etudiant != null && etudiant.getOption() != null && etudiantBusiness.addEtudiant(etudiant)) {
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    // 2) Récupération de la liste de tous les étudiants
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEtudiants() {
        List<Etudiant> list = etudiantBusiness.getAllEtudiants();
        return Response.status(200).entity(list).build();
    }

    // 6) Récupération de la liste des étudiants inscrits à une option donnée (XML)
    @GET
    @Path("option")
    @Produces(MediaType.APPLICATION_XML)
    public Response getEtudiantsByOption(@QueryParam("codeOption") Integer codeOption) {
        if (codeOption == null) {
            return Response.status(404).build();
        }
        Option option = optionBusiness.getOptionByCode(codeOption);
        if (option == null) {
            return Response.status(404).build();
        }
        List<Etudiant> etudiants = etudiantBusiness.getEtudiantsByOption(option);
        return Response.status(200).entity(new EtudiantList(etudiants)).build();
    }

    // 3) Récupération d'un étudiant ayant un identifiant spécifique
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEtudiantByIdentifiant(@PathParam("id") String id) {
        Etudiant etudiant = etudiantBusiness.getEtudiantByIdentifiant(id);
        if (etudiant != null) {
            return Response.status(200).entity(etudiant).build();
        }
        return Response.status(404).build();
    }

    // 4) Suppression d'un étudiant ayant un identifiant spécifique
    @DELETE
    @Path("{id}")
    public Response deleteEtudiant(@PathParam("id") String id) {
        if (etudiantBusiness.deleteEtudiant(id)) {
            return Response.status(204).build();
        }
        return Response.status(404).build();
    }

    // 5) Modification d'un étudiant ayant un identifiant spécifique
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateEtudiant(@PathParam("id") String id, Etudiant etudiant) {
        if (etudiant != null && etudiantBusiness.updateEtudiant(id, etudiant)) {
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }
}
