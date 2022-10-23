package swrl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.vocab.OWLFacet;

public class build {
	private static build ex = new build();
		
	public static void main(String[] args)
	{
		try {
			ex.buildOntology();
			System.out.println("done");
		} catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	/*
	 * Basically, the mapping is the following
	 * 1) Database schema can be converted to a set of classes, datatype properties and object properties (TBox) 
	 * Table->class
	 * Attribute->restriction on datatype property
	 * foreign key->restriction on object property
	 *    
	 * 2) ABox:
	 * records (table rows) -> individuals of appropriate classes
	 * attribute values->literals linked by datatype properties corresponding to attribute names
	 * foreign key values -  assertions on relations between individuals
	 */
 
    /////////////////////////////////////////////////////////////////////////////////

    public void buildOntology() throws OWLOntologyCreationException, OWLOntologyStorageException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        String base = "file:/home/mnaeem/workspace/owltutorial/onto1.owl";
        OWLOntology ont = man.createOntology(IRI.create(base));
        // We want to add an axiom to our ontology that states that adults have
        // an age greater than 18. To do this, we will create a restriction
        // along a hasAge property, with a filler that corresponds to the set of
        // integers greater than 18. First get a reference to our hasAge
        // property
        OWLDataFactory factory = man.getOWLDataFactory();
        
        int i = 0;
        String[] arr = new String[3];
        arr[0] = "#Quotation";
        arr[1] = "#Supplier";
        arr[2] = "#Tube";
        
        OWLClass[] cls = new OWLClass[arr.length];
        OWLDeclarationAxiom[] dec = new OWLDeclarationAxiom[arr.length];
        
        for (i=0; i<cls.length; i++) {
            cls[i] = factory.getOWLClass(IRI.create(base + arr[i]));
            dec[i] = factory.getOWLDeclarationAxiom(cls[i]);
            man.addAxiom(ont, dec[i]);
        }
        
        ////////////////////////////
        OWLAxiom axiom = factory.getOWLSubClassOfAxiom(cls[0], cls[1]);
		// add the axiom to the ontology.
		AddAxiom addAxiom = new AddAxiom(ont, axiom);
		// We now use the manager to apply the change
		man.applyChange(addAxiom);
        
        ////////////////////////////////
        
       
        OWLDataProperty hasRange = factory.getOWLDataProperty(IRI.create("hasRange"));
        OWLDeclarationAxiom d = factory.getOWLDeclarationAxiom(hasRange);
      //  factory.getOWLDataPropertyAssertionAxiom(hasRange, , intDataType);  
        
        man.addAxiom(ont, d);
        
        
        OWLDataProperty hasAge = factory.getOWLDataProperty(IRI.create("hasAge"));
        OWLFunctionalDataPropertyAxiom funcAx = factory.getOWLFunctionalDataPropertyAxiom(hasAge);
        man.applyChange(new AddAxiom(ont, funcAx));
        OWLFacet facet = OWLFacet.MIN_INCLUSIVE;
        OWLDatatype intDatatype = factory.getIntegerOWLDatatype();
        // Create the value "18", which is an int.
        OWLLiteral eighteenConstant = factory.getOWLLiteral(18);

        OWLDataRange intGreaterThan18 = factory.getOWLDatatypeRestriction(
                intDatatype, facet, eighteenConstant);
        OWLClassExpression thingsWithAgeGreaterOrEqualTo18 = factory
                .getOWLDataSomeValuesFrom(hasAge, intGreaterThan18);

        
        OWLClass adult = factory.getOWLClass(IRI.create(base + "#Adult"));
        OWLSubClassOfAxiom ax = factory.getOWLSubClassOfAxiom(adult,  thingsWithAgeGreaterOrEqualTo18);
        man.applyChange(new AddAxiom(ont, ax));

        

        ///////////////////////////////////////////////////////////
        OWLObjectProperty hasQuotation = factory.getOWLObjectProperty(IRI.create("#hasQuotation"));
        OWLIndividual sup1 = factory.getOWLNamedIndividual(IRI.create("#Sup1"));
        OWLIndividual sup2 = factory.getOWLNamedIndividual(IRI.create("#Sup2"));
        
        OWLObjectPropertyAssertionAxiom axiom1 = factory.
        		getOWLObjectPropertyAssertionAxiom(hasQuotation, sup1, sup2);
        man.applyChange( new AddAxiom(ont, axiom1));
        
        Set<OWLAxiom> domainsAndRanges = new HashSet<OWLAxiom>();
        domainsAndRanges.add(factory.getOWLObjectPropertyDomainAxiom(hasQuotation, cls[0]));
        domainsAndRanges.add(factory.getOWLObjectPropertyRangeAxiom(hasQuotation, cls[2]));
        man.addAxioms(ont, domainsAndRanges);
        
        ////////////////////////////////////////////////////////////////////////   

        OWLObjectProperty drawsQuotation = factory.getOWLObjectProperty(IRI.create("#drawsQuotation"));
        
        OWLObjectPropertyAssertionAxiom axiom2 = factory.
        		getOWLObjectPropertyAssertionAxiom(hasQuotation, sup2, sup1);
        man.applyChange( new AddAxiom(ont, axiom2));
        
   //     Set<OWLAxiom> domainsAndRanges = new HashSet<OWLAxiom>();
        domainsAndRanges.add(factory.getOWLObjectPropertyDomainAxiom(drawsQuotation, cls[2]));
        domainsAndRanges.add(factory.getOWLObjectPropertyRangeAxiom(drawsQuotation, cls[0]));
        man.addAxioms(ont, domainsAndRanges);
        
        ////////////////////////////////////////////////////////////////////////   
        man.addAxiom(ont, factory.getOWLInverseObjectPropertiesAxiom(hasQuotation, drawsQuotation));
        ////////////////////////////////////////////////////////////////////////
        
        SWRLVariable var = factory.getSWRLVariable(IRI.create("#x"));
        SWRLRule rule = factory.getSWRLRule(
                Collections.singleton(factory.getSWRLClassAtom(cls[0], var)),
                Collections.singleton(factory.getSWRLClassAtom(cls[1], var)));
        
        man.applyChange(new AddAxiom(ont, rule));
        OWLObjectProperty prop = factory.getOWLObjectProperty(IRI
                .create("#propA"));
        OWLObjectProperty propB = factory.getOWLObjectProperty(IRI
                .create("#propB"));
        SWRLObjectPropertyAtom propAtom = factory.getSWRLObjectPropertyAtom(
                prop, var, var);
        SWRLObjectPropertyAtom propAtom2 = factory.getSWRLObjectPropertyAtom(
                propB, var, var);
        Set<SWRLAtom> antecedent = new HashSet<SWRLAtom>();
        antecedent.add(propAtom);
        antecedent.add(propAtom2);
        SWRLRule rule2 = factory.getSWRLRule(antecedent,
                Collections.singleton(propAtom));
        man.applyChange(new AddAxiom(ont, rule2));
        
        ////////////////////////////////////////////////////////////////////////
        man.saveOntology(ont);
        
    }

    
    

	///////////////////////////////////////////////////////
}
