/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.iit.sat.itmd4515.wgu10;

import edu.iit.sat.itmd4515.wgu10.domain.Adopter;
import edu.iit.sat.itmd4515.wgu10.domain.Pet;
import edu.iit.sat.itmd4515.wgu10.domain.PetType;
import java.time.LocalDate;
import java.time.Month;
import javax.persistence.RollbackException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author wenganGu
 */
public class PetJPATest extends AbstractJPATest {

    // example "sunny day" test - expecting success
    @Test
    public void testCreateShouldPass() {
        // create an entity
        Pet cat = new Pet("Fluffy", LocalDate.of(2020, Month.DECEMBER, 12), PetType.FELINE);
        tx.begin();
        em.persist(cat);
        tx.commit();

        // assert success
        assertNotNull(cat.getId());
        assertTrue(cat.getId() >= 1l);

        // clean up after yourself
        tx.begin();
        em.remove(cat);
        tx.commit();
    }

    // example "rainy day" test - expecting failure
    @Test
    public void testCreateShouldFail() {
        // create an entity
        Pet cat = new Pet("TESTPET", LocalDate.of(2020, Month.DECEMBER, 12), PetType.FELINE);

        assertThrows(RollbackException.class, () -> {
            tx.begin();
            em.persist(cat);
            tx.commit();
        });
    }

    @Test
    public void testRead() {
        // find a pet from the database
        Pet test = em
                .createNamedQuery("Pet.findPetByName", Pet.class)
                .setParameter("NAME", "TESTPET")
                .getSingleResult();
        
        assertNotNull(test);
        assertEquals("TESTPET", test.getName());
    }

    @Test
    public void testUpdate() {
        // find a pet from the database
        Pet test = em
                .createNamedQuery("Pet.findPetByName", Pet.class)
                .setParameter("NAME", "TESTPET")
                .getSingleResult();

        // update something about this, but be careful - the afterEach em.remove
        // still needs to work.  In my case - that means watch out for the name
        tx.begin();
        test.setType(PetType.FISH);
        tx.commit();
        
        // next, read it back from the database and assert your update was OK
        test = em.find(Pet.class, test.getId());
        assertEquals(PetType.FISH, test.getType());
    }

    @Test
    public void testDelete() {
        // create an entity
        Pet cat = new Pet("Fluffy", LocalDate.of(2020, Month.DECEMBER, 12), PetType.FELINE);
        tx.begin();
        em.persist(cat);
        tx.commit();

        // assert created OK
        assertNotNull(cat.getId());
        
        // remove it
        tx.begin();
        em.remove(cat);
        tx.commit();
        
        // assert it was removed OK by trying to re-find it from the database
        cat = em.find(Pet.class, cat.getId());
        assertNull(cat);
    }

    @Test
    public void testAdopterPetManyToManyBiDirectionalRelationship(){
        // create entities to test
        Pet cat = new Pet("Fluffy", LocalDate.of(2020, Month.DECEMBER, 12), PetType.FELINE);
        Adopter adopter = new Adopter("Scott", "Spyrison", LocalDate.of(1950, Month.MARCH, 12));
        
        // ex 1 - I demonstrated, without any relationship management these are just two indepednent entities with no relationship
        
        // ex 2 - Add the adopter to the inverse side of the relationship.  If you only do this, and you don't manage the owning side, then you will have inconsistent database updates
        //cat.getOwners().add(adopter);
        
        // ex 3 - Manage the owning side, but skip the non-owning inverse side
        //adopter.getAdoptedPets().add(cat);
        
        // ex 4 - Manage both sides of the relationship per JPA requirements
        // and Prof Scott lecture!
//        adopter.getAdoptedPets().add(cat);
//        cat.getOwners().add(adopter);
        adopter.addAdoptedPet(cat);
        
        tx.begin();
        em.persist(cat);
        em.persist(adopter);
        tx.commit();
        
        adopter = em.find(Adopter.class, adopter.getId());
        cat = em.find(Pet.class, cat.getId());
        // ex 3 continued - let's explore the persistence context for this relationship
        // this is navigation from the OWNING side
        //System.out.println("OWNING SIDE: " + adopter.getAdoptedPets().toString());

        //for( Pet p : adopter.getAdoptedPets()){
            // this is navigation from the INVERSE side
            //System.out.println("INVERSE SIDE: " + p.getOwners().toString());
        //}
        // ex 3 fails in the PersistenceContext because we didn't manage both sides of the relationships.
        // PersistenceContext is out of sync with the database update
        
        // ex 4
        adopter = em.find(Adopter.class, adopter.getId());
        // this is navigation from the OWNING side
        System.out.println("OWNING SIDE: " + adopter.getAdoptedPets().toString());

        for( Pet p : adopter.getAdoptedPets()){
            // this is navigation from the INVERSE side
            System.out.println("INVERSE SIDE: " + p.getOwners().toString());
        }
        // ex 4 - our database update is consistent with both sides of our relationship
        // and persistence context is in sync with database

        // travel both sides of the relationship and assert what we find is expected
        assertTrue(adopter.getAdoptedPets().size() == 1);
        assertTrue(cat.getOwners().size() == 1);
        assertEquals(cat.getName(), adopter.getAdoptedPets().get(0).getName());
        assertEquals(adopter.getId(), cat.getOwners().get(0).getId());
        
        // clean up after ourselves - remove test data
        tx.begin();
        // first, remove from the collection
        adopter.removeAdoptedPet(cat);
        // then, remove the non-owning entity
        em.remove(cat);
        // finally, remove the owning entity
        em.remove(adopter);
        tx.commit();
        
    }
    
    
}
