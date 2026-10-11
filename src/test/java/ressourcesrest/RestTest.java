package ressourcesrest;

import entities.Etudiant;
import entities.EtudiantList;
import entities.Option;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RestTest {

    private restOption optionResource;
    private restEtudiant etudiantResource;

    @BeforeEach
    public void setup() {
        optionResource = new restOption();
        etudiantResource = new restEtudiant();
    }

    @Test
    public void testA1_AddOption() {
        Option op = new Option(10, "Intelligence Artificielle", "Informatique", "Dr. AI", 30, 2, 25);
        Response response = optionResource.ajoutOption(op);
        assertEquals(200, response.getStatus());

        // Test with null
        Response responseNull = optionResource.ajoutOption(null);
        assertEquals(404, responseNull.getStatus());
    }

    @Test
    public void testA2_GetAllOptions() {
        Response response = optionResource.getOptions(null);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
        List<?> list = (List<?>) response.getEntity();
        assertTrue(list.size() > 0);
    }

    @Test
    public void testA3_GetOptionsByDomaine() {
        Response response = optionResource.getOptions("Mathématiques");
        assertEquals(200, response.getStatus());
        List<Option> list = (List<Option>) response.getEntity();
        assertNotNull(list);
        assertFalse(list.isEmpty());
        for (Option op : list) {
            assertEquals("Mathématiques", op.getDomaine());
        }
    }

    @Test
    public void testA4_DeleteOption() {
        // Delete existing option (e.g. code 5)
        Response response = optionResource.deleteOption(5);
        assertEquals(204, response.getStatus());

        // Delete non-existent
        Response responseNotFound = optionResource.deleteOption(9999);
        assertEquals(404, responseNotFound.getStatus());
    }

    @Test
    public void testA5_UpdateOption() {
        Option updated = new Option(1, "Informatique", "Informatique", "M. Nouveau Responsable Informatique", 30, 1, 35);
        Response response = optionResource.updateOption(1, updated);
        assertEquals(200, response.getStatus());

        // Update non-existent
        Response responseNotFound = optionResource.updateOption(9999, updated);
        assertEquals(404, responseNotFound.getStatus());
    }

    @Test
    public void testA6_GetOptionByCode() {
        Response response = optionResource.getOptionByCode(1);
        assertEquals(200, response.getStatus());
        Option op = (Option) response.getEntity();
        assertNotNull(op);
        assertEquals(1, op.getCodeOption());

        Response responseNotFound = optionResource.getOptionByCode(9999);
        assertEquals(404, responseNotFound.getStatus());
    }

    @Test
    public void testB1_AddEtudiant() {
        Option op = new Option();
        op.setCodeOption(1);
        Etudiant etudiant = new Etudiant("I0010", "Etudiant 1", "Etudiant 1", op, 2023, "etudiant1@example.com");

        Response response = etudiantResource.addEtudiant(etudiant);
        assertEquals(200, response.getStatus());

        // Add with non-existing option
        Option invalidOp = new Option();
        invalidOp.setCodeOption(9999);
        Etudiant invalidEtudiant = new Etudiant("I9999", "Fake", "Student", invalidOp, 2023, "fake@example.com");
        Response responseFail = etudiantResource.addEtudiant(invalidEtudiant);
        assertEquals(404, responseFail.getStatus());
    }

    @Test
    public void testB2_GetAllEtudiants() {
        Response response = etudiantResource.getAllEtudiants();
        assertEquals(200, response.getStatus());
        List<?> list = (List<?>) response.getEntity();
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }

    @Test
    public void testB3_GetEtudiantByIdentifiant() {
        Response response = etudiantResource.getEtudiantByIdentifiant("I001");
        assertEquals(200, response.getStatus());
        Etudiant etudiant = (Etudiant) response.getEntity();
        assertNotNull(etudiant);
        assertEquals("I001", etudiant.getIdentifiant());

        Response responseNotFound = etudiantResource.getEtudiantByIdentifiant("NONEXISTENT");
        assertEquals(404, responseNotFound.getStatus());
    }

    @Test
    public void testB4_DeleteEtudiant() {
        Response response = etudiantResource.deleteEtudiant("I002");
        assertEquals(204, response.getStatus());

        Response responseNotFound = etudiantResource.deleteEtudiant("NONEXISTENT");
        assertEquals(404, responseNotFound.getStatus());
    }

    @Test
    public void testB5_UpdateEtudiant() {
        Option op = new Option();
        op.setCodeOption(1);
        Etudiant updated = new Etudiant("I001", "Etudiant 5", "Etudiant 5", op, 2023, "etudiant5@example.com");
        Response response = etudiantResource.updateEtudiant("I001", updated);
        assertEquals(200, response.getStatus());

        Response responseNotFound = etudiantResource.updateEtudiant("NONEXISTENT", updated);
        assertEquals(404, responseNotFound.getStatus());
    }

    @Test
    public void testB6_GetEtudiantsByOptionAndXmlSerialization() throws Exception {
        // Query existing option code 1
        Response response = etudiantResource.getEtudiantsByOption(1);
        assertEquals(200, response.getStatus());
        EtudiantList etudiantList = (EtudiantList) response.getEntity();
        assertNotNull(etudiantList);

        // Verify XML marshalling works as expected
        JAXBContext context = JAXBContext.newInstance(EtudiantList.class, Etudiant.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter writer = new StringWriter();
        marshaller.marshal(etudiantList, writer);
        String xml = writer.toString();

        assertTrue(xml.contains("<etudiants>"));
        assertTrue(xml.contains("<etudiant>"));
        assertFalse(xml.contains("<option>")); // getOption is @XmlTransient

        // Query non-existing option
        Response responseNotFound = etudiantResource.getEtudiantsByOption(9999);
        assertEquals(404, responseNotFound.getStatus());
    }
}
