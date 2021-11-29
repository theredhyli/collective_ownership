import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Subject that may obtain objects
 */
public class Subject {

    /**
     * Constructor with available objects list
     * @param availableToOwnObjects - objects that subject may obtain
     */
    public Subject(List<Integer> availableToOwnObjects) {
        this.availableToOwnObjects = availableToOwnObjects;
    }

    /**
     * Constructor with available objects list and LOW_PRIO flag
     * @param availableToOwnObjects - objects that subject may obtain
     * @param LOW_PRIO - flag that represents that subject is willing to obtain object only if there is no other candidates
     */
    public Subject(List<Integer> availableToOwnObjects, Boolean LOW_PRIO) {
        this.availableToOwnObjects = availableToOwnObjects;
        this.LOW_PRIO = LOW_PRIO;
    }

    /**
     * Objects that subject may obtain
     */
    private final List<Integer> availableToOwnObjects;

    /**
     * Objects that subject is currently obtain
     */
    private final List<Integer> ownedObjects = new ArrayList<>();

    /**
     * Flag that represents that subject is willing to obtain object only if there is no other candidates
     * false by default
     */
    private Boolean LOW_PRIO = false;

    void obtainObject(Integer object) {
        ownedObjects.add(object);
    }

    Integer ownedObjectsSize() {
        return ownedObjects.size();
    }

    /**
     * When comparing objects we compare values of availableToOwnObjects and LOW_PRIO
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return availableToOwnObjects.equals(subject.availableToOwnObjects) && LOW_PRIO.equals(subject.LOW_PRIO);
    }

    /**
     * When calculating hash code we use values of availableToOwnObjects and LOW_PRIO
     */
    @Override
    public int hashCode() {
        return Objects.hash(availableToOwnObjects, LOW_PRIO);
    }

    @Override
    public String toString() {
        return "Subject{" +
                "availableToOwnObjects=" + availableToOwnObjects +
                "LOW_PRIO=" + LOW_PRIO +
                '}';
    }

    public List<Integer> getAvailableToOwnObjects() {
        return availableToOwnObjects;
    }

    public Boolean getLOW_PRIO() {
        return LOW_PRIO;
    }

    public List<Integer> getOwnedObjects() {
        return ownedObjects;
    }
}
