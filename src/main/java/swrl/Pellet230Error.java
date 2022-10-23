package swrl;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

public class Pellet230Error {

    private static final String BASE_URL = "pellet230err.owl";

    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File fl = new File(BASE_URL);
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(fl);
        OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology, new SimpleConfiguration());
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLClass aClass = factory.getOWLClass(IRI.create(BASE_URL+"#A"));
        System.out.println("it will hang on the next line ...");
        NodeSet<OWLNamedIndividual> nodeSet = reasoner.getInstances(aClass, false);
        System.out.println("Nodes: " + nodeSet.getNodes().size());
        System.out.println("this is never printed");
    }
}