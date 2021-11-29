import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class UnitTests {
    @Test
    public void addSingleSubjectTest() {
        OwnershipDistribution distribution = new OwnershipDistribution(10);
        Subject testSubject = new Subject(Arrays.asList(2,3));
        distribution.addSubject(testSubject);
        List<Subject> expectedDistribution = Arrays.asList(null, null, testSubject, testSubject, null, null, null, null, null, null);
        Assertions.assertEquals(distribution.getObjectsOwners(), expectedDistribution);
    }

    @Test
    public void distributionForTwoSubjectsTest() {
        OwnershipDistribution distribution = new OwnershipDistribution(10);

        Subject testSubject = new Subject(Arrays.asList(2,3));
        distribution.addSubject(testSubject);

        Subject testSubjectWithMoreAvailableObjects = new Subject(Arrays.asList(3,4,5));
        distribution.addSubject(testSubjectWithMoreAvailableObjects);

        List<Subject> expectedDistribution = Arrays.asList(null, null, testSubject, testSubject, testSubjectWithMoreAvailableObjects, testSubjectWithMoreAvailableObjects, null, null, null, null);
        Assertions.assertEquals(distribution.getObjectsOwners(), expectedDistribution);
        Assertions.assertEquals(distribution.getSubjects(), Arrays.asList(testSubject, testSubjectWithMoreAvailableObjects));
    }

    @Test
    public void removeSingleSubjectTest() {
        OwnershipDistribution distribution = new OwnershipDistribution(10);
        Subject testSubjectToAdd = new Subject(Arrays.asList(2,3));
        distribution.addSubject(testSubjectToAdd);

        Subject testSubjectToRemove = new Subject(Arrays.asList(2,3));
        distribution.removeSubject(testSubjectToRemove);

        List<Subject> expectedDistribution = Arrays.asList(null, null, null, null, null, null, null, null, null, null);
        Assertions.assertEquals(distribution.getObjectsOwners(), expectedDistribution);
        Assertions.assertTrue(distribution.getSubjects().isEmpty());
    }

    @Test
    public void removeSubjectAndDistributeOwnershipTest() {
        OwnershipDistribution distribution = new OwnershipDistribution(10);
        Subject subjectToAdd = new Subject(Arrays.asList(2,3));
        distribution.addSubject(subjectToAdd);
        Subject subjWithMoreAvailableObjects = new Subject(Arrays.asList(2,3,5));
        distribution.addSubject(subjWithMoreAvailableObjects);

        Subject subjectToRemove = new Subject(Arrays.asList(2,3));
        distribution.removeSubject(subjectToRemove);

        List<Subject> expectedDistribution = Arrays.asList(null, null, subjWithMoreAvailableObjects, subjWithMoreAvailableObjects, null, subjWithMoreAvailableObjects, null, null, null, null);
        Assertions.assertEquals(distribution.getObjectsOwners(), expectedDistribution);
        Assertions.assertEquals(distribution.getSubjects(), List.of(subjWithMoreAvailableObjects));
    }

    @Test
    public void removeNotAddedObjectFailsTest() {
        OwnershipDistribution distribution = new OwnershipDistribution(10);
        Subject subjectToRemove = new Subject(Arrays.asList(2,3));

        Assertions.assertThrows(IllegalStateException.class, () -> distribution.removeSubject(subjectToRemove));
        List<Subject> expectedDistribution = Arrays.asList(null, null, null, null, null, null, null, null, null, null);
        Assertions.assertEquals(distribution.getObjectsOwners(), expectedDistribution);
        Assertions.assertTrue(distribution.getSubjects().isEmpty());
    }

    @Test
    public void fairDistributionTest() {
        OwnershipDistribution distribution = new OwnershipDistribution(10);
        List<Subject> subjectsToAdd = Arrays.asList(new Subject(Arrays.asList(2,3)), new Subject(Arrays.asList(2,3,4)), new Subject(Arrays.asList(3,4)));
        subjectsToAdd.forEach(distribution::addSubject);

        List<Subject> expectedDistribution = Arrays.asList(null, null, subjectsToAdd.get(0), subjectsToAdd.get(2), subjectsToAdd.get(1), null, null, null, null, null);
        Assertions.assertEquals(distribution.getObjectsOwners(), expectedDistribution);
        Assertions.assertEquals(distribution.getSubjects(), subjectsToAdd);
    }

    @Test
    public void addExistingSubjectFailsTest() {
        OwnershipDistribution distribution = new OwnershipDistribution(10);
        Subject subjectToAdd = new Subject(Arrays.asList(2,3));
        distribution.addSubject(subjectToAdd);

        Subject sameSubjectToAdd = new Subject(Arrays.asList(2,3));

        Assertions.assertThrows(IllegalStateException.class, () -> distribution.addSubject(sameSubjectToAdd));
        Assertions.assertEquals(distribution.getSubjects(), List.of(subjectToAdd));
    }

    @Test
    public void addSubjectWithOutOfBoundsObjectFailsTest() {
        OwnershipDistribution distribution = new OwnershipDistribution(10);
        Subject subjectToAdd = new Subject(Arrays.asList(9,10));

        Assertions.assertThrows(IllegalStateException.class, () -> distribution.addSubject(subjectToAdd));
        List<Subject> expectedDistribution = Arrays.asList(null, null, null, null, null, null, null, null, null, null);
        Assertions.assertEquals(distribution.getObjectsOwners(), expectedDistribution);
        Assertions.assertTrue(distribution.getSubjects().isEmpty());
    }

    @Test
    public void addLowPrioritySubjectDistributionTest() {
        OwnershipDistribution distribution = new OwnershipDistribution(10);
        List<Subject> subjectsToAdd = Arrays.asList(new Subject(Arrays.asList(2,3,4)), new Subject(Arrays.asList(2,3), true));
        subjectsToAdd.forEach(distribution::addSubject);

        List<Subject> expectedDistribution = Arrays.asList(null, null, subjectsToAdd.get(0), subjectsToAdd.get(0), subjectsToAdd.get(0), null, null, null, null, null);
        Assertions.assertEquals(distribution.getObjectsOwners(), expectedDistribution);
        Assertions.assertEquals(distribution.getSubjects(), subjectsToAdd);
    }

    @Test
    public void distributeSubjWithLowPrioAfterRemoveTest() {
        OwnershipDistribution distribution = new OwnershipDistribution(10);
        List<Subject> subjectsToAdd = Arrays.asList(new Subject(Arrays.asList(2,3,4)), new Subject(Arrays.asList(2,3), true));
        subjectsToAdd.forEach(distribution::addSubject);

        distribution.removeSubject(new Subject(Arrays.asList(2,3,4)));

        List<Subject> expectedDistribution = Arrays.asList(null, null, subjectsToAdd.get(1), subjectsToAdd.get(1), null, null, null, null, null, null);
        Assertions.assertEquals(distribution.getObjectsOwners(), expectedDistribution);
        Assertions.assertEquals(distribution.getSubjects(), List.of(subjectsToAdd.get(1)));
    }
}
