package fss.lsi3.soa;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;

public class Main {

    private static final String XML_FILE = "src/main/resources/bibliotheque.xml";
    private static final String XSD_FILE = "src/main/resources/bibliotheque.xsd";

    public static void main(String[] args) throws Exception {

        // 1 : validation
        System.out.println("1. Validation XML / XSD :");
        if (!validerXml(XML_FILE, XSD_FILE)) {
            System.out.println("Arret : corrigez le XML avant de l'interroger.");
            return;
        }

        // 2 : XPath
        System.out.println();
        System.out.println("2. Interrogation XPath :");

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new File(XML_FILE));
        XPath xpath = XPathFactory.newInstance().newXPath();

        lister(doc, xpath, "Titres de tous les livres",
                "//livre/titre");
        lister(doc, xpath, "Livres disponibles",
                "//livre[@disponible='true']/titre");
        lister(doc, xpath, "Étudiants actifs (noms)",
                "//etudiant[@actif='true']/nom");
        lister(doc, xpath, "Livres publiés après 2005",
                "//livre[annee > 2005]/titre");
        lister(doc, xpath, "Livres de la catégorie Informatique",
                "//livre[categorie='Informatique']/titre");
        lister(doc, xpath, "Livres avec moins de 2 exemplaires",
                "//livre[exemplaires < 2]/titre");
        lister(doc, xpath, "Étudiants de la filière LSI3",
                "//etudiant[filiere='LSI3']/nom");
        lister(doc, xpath, "Livres empruntés par l'étudiant E001",
                "//livre[@id=//emprunt[@etudiantRef='E001']/@livreRef]/titre");

        double nb = (Double) xpath.evaluate("count(//livre)", doc, XPathConstants.NUMBER);
        System.out.println();
        System.out.println("+ "+"Nombre total de livres : " + (int) nb);
    }

    /** Valide le fichier XML par rapport au schéma XSD */
    static boolean validerXml(String xmlPath, String xsdPath) {
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlPath)));
            System.out.println("Le document XML est Valide.");
            return true;
        } catch (SAXException e) {
            System.out.println("Document Invalide : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return false;
    }

    /** Exécute une requête XPath et affiche le texte de chaque noeud trouvé */
    static void lister(Document doc, XPath xpath, String description, String expression)
            throws Exception {
        XPathExpression expr = xpath.compile(expression);
        NodeList noeuds = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        System.out.println();
        System.out.println("+ "+description + "  [" + expression + "]");
        for (int i = 0; i < noeuds.getLength(); i++) {
            System.out.println("  - " + noeuds.item(i).getTextContent());
        }
    }
}