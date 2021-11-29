import java.util.*;
import java.util.stream.Collectors;

/**
 * Provide fair distribution of objects between new subjects
 * @author НАПИСАТЬ СВОЕ ИМЯ.ФАМИЛИЮ НА ЛАТИНИЦЕ (пример: artur.parpibaev)
 */
public class OwnershipDistribution {

    /**
     * @param availableObjectsSize - size of objects which are initialized from 0 to availableObjectsSize - 1
     */
    // УДАЛИТЬ - мы инициализируем класс и передаем диапазон, в котором могут передаваться наши объекты. Другими словами, в задании это указано как SN
    public OwnershipDistribution(int availableObjectsSize) {
        this.objectsOwners = new ArrayList<>();
        for (int i = 0; i < availableObjectsSize; i++) {
            objectsOwners.add(null);
        }
    }

    /**
     * Current state of objects ownership where list index is object and value is subject that obtains that object
     * For objects without owners value is null
     * Example: availableObjectsSize = 3, initial state: [0] = null, [1] = null, [2] = null
     * After addition of Subject(Arrays.asList(0,1)): [0] = Subject(0,1), [1] = Subject(0,1), [2] = null
     */
    // УДАЛИТЬ - по сути это структура ключ-значение, в которой по индексу списка (0,1,2,3) находится номер объекта
    // (это же и есть сам объект, тк кроме его номера нам ничего о нем знать не нужно)
    // а по значению - владелец (субъект) этого объекта. Пример указан в комментарии выше.
    private final List<Subject> objectsOwners;

    /**
     * All subjects that were added
     */
    // УДАЛИТЬ - список, отображающий все добавленные субъекты. Когда субъект удаляется, он убирается из этого списка.
    private final List<Subject> subjects = new ArrayList<>();

    /**
     * Add subject and obtain available objects according to fair distribution
     * @param newSubject - new subject to add
     * @throws IllegalStateException - in cases of
     * 1) subject is already exists
     * 2) some of subject's available object is out of predefined bounds
     */
    public void addSubject(Subject newSubject) {
        // УДАЛИТЬ - проверяем, нет ли такого субъекта уже в нашем списке, то есть не добавляли его уже раньше. Если да - кидаем исключение
        if (subjects.contains(newSubject)) {
            throw new IllegalStateException("Subject with such available objects already exists");
        }
        // УДАЛИТЬ - проверяем, есть ли в списке объектов, которые может занять субъект, такие, которые находятся не в рамках изначально заданного диапазона
        // Например, у нас изначально задан диапазон 5, то есть будет список объектов - [0,1,2,3,4]. Мы добавляем Subject (4,5), а в нашем списке не может
        // быть объекта с индексом 5, поэтому мы кидаем исклчение
        newSubject.getAvailableToOwnObjects().forEach((object) -> {
            if (object < 0 || object > (objectsOwners.size() - 1)) {
                throw new IllegalStateException("Object " + object + " in subject " + newSubject + " is out of bounds of initialized objects");
            }
        });
        // УДАЛИТЬ - объекты, которые мы рассмотрим потом, потому что они уже заняты
        List<Integer> obtainedObjects = new ArrayList<>();
        // УДАЛИТЬ ВСЕ КОММЕНТЫ НИЖЕ - теперь наконец переходим к алгоритму распределния при добавлении
        // Допустим, у нас уже есть ситуация из примера, когда мы добавили S1 и добавляем S2
        // То есть текущее значение objectsOwners = [0] = null, [1] = null, [2] = S1, [3] = S1, [4] = null и т.д до [9], они тоже все null
        // Берем доступные для взятия в работу объекты нового субъекта (в нашем случае S2(2,4,5)) и для каждого из этих объектов в цикле делаем следующее
        newSubject.getAvailableToOwnObjects().forEach((object) -> {
            // если в нашем списке objectsOwners у такого объекта (2) нет владельцев (а он есть - S1, поэтому идем в else)
            // если в нашем списке objectsOwners у такого объекта (4) нет владельцев, то
            // если в нашем списке objectsOwners у такого объекта (5) нет владельцев, то
            if (objectsOwners.get(object) == null) {
                // присваиваем объекту без владельца, собственно, владельца, то есть: objectsOwners[4] = S2 и S2 внутри себя тоже запоминает, что он занял этот объект (obtainObject)
                // присваиваем объекту без владельца, собственно, владельца, то есть: objectsOwners[5] = S2 и S2 внутри себя тоже запоминает, что он занял этот объект (obtainObject)
                reassignObject(object, newSubject);
            } else {
                // сюда у нас попал только объект 2, у которого уже есть владелец S1
                // если мы заранее обозначили низкий приоритет, то просто оставляем его у текущего владльца,
                // но в данном случае у нас субъект с обычным приоритетом
                if (newSubject.getLOW_PRIO()) {
                    return;
                }
                // добавляем объект 2 в список для уже занятых объектов, который рассмотрим после того, как займем все возможные объекты без владельцев
                // (то есть дойдем до конца текущего цикла forEach)
                obtainedObjects.add(object);
            }
        });
        // для каждого из уже занятых объектов (у нас это только 2) смотрим, можем ли мы их отобрать у другого субъекта
        obtainedObjects.forEach((delayedObject) -> {
            // если у субъекта, который владеет объектом 2, больше объектов, чем у нового субъекта с разницей минимум в два, то мы у него объект забираем
            // если объектов у него, например, 2, а у нас тоже 2 (или 3, а у нас 2), то ничего не делаем и оставляем объект ему
            // так мы обеспечиваем честное распределение
            if (objectsOwners.get(delayedObject).ownedObjectsSize() - 1 > newSubject.ownedObjectsSize()) {
                reassignObject(delayedObject, newSubject);
            }
        });
        // в конце добавляем наш новый субъект в список субъектов
        subjects.add(newSubject);
    }

    /**
     * Remove subject and distribute released objects between remaining subjects
     * @param subjectToRemove - new subject to remove
     * @throws IllegalStateException - if subject wasn't added before
     */
    void removeSubject(Subject subjectToRemove) {
        // УдАЛИТЬ ВСЕ КОММЕНТЫ НИЖЕ - сначала проверяем, был ли такой объект добавлен раньше и если нет, то кидаем исключение
        if (!subjects.contains(subjectToRemove)) {
            throw new IllegalStateException("Subject " + subjectToRemove.toString() + " isn't available in list of existing subjects: " + subjects);
        }
        // находим такой же субъект среди ранее добавленных субъектов, тк нам нужно знать объекты которыми он владеет
        Subject existingSubjectToRemove = subjects.stream().filter(subject -> subject.equals(subjectToRemove)).collect(Collectors.toList()).get(0);
        // убираем субъект из списка всех субъектов
        subjects.remove(subjectToRemove);
        // Теперь переходим к алгоритму удаления субъекта и распределения освободившихся объектов
        // Допустим, у нас уже есть пример из задания с удалением объекта S2(2,4,5)
        // Текущее состояние objectOwners - [0] = null, [1] = null, [2] = S3, [3] = S1, [4] = S2, [5] = S2, [6] = null и тд до [9], тк все null
        // для каждого из объектов, которыми на данный момент владеет субъект (то есть 4,5)
        existingSubjectToRemove.getOwnedObjects().forEach((object) -> {
            // убираем субъект как владельца объекта
            objectsOwners.set(object, null);
            // выбираем для этого объекта (4) кандидата-субъекта, фильтруя всех субъектов по тому, могут ли они владеть этим объектом
            // выбираем для этого объекта (5) кандидата-субъекта, фильтруя всех субъектов по тому, могут ли они владеть этим объектом
            List<Subject> objectCandidates = subjects
                .stream()
                .filter(subject -> subject.getAvailableToOwnObjects().contains(object)).collect(Collectors.toList());
            // если нет кандидата, то не присваиваем этому объекту никакого владельца
            // в нашем примере для объектов 4,5 будет только этот ранний выход!
            if (objectCandidates.isEmpty()) {
                return;
            }
            // фильтруем из списка кандидатов тех, у кого определено свойство LOW_PRIO, то есть имеют низкий приоритет
            List<Subject> subjectsWithLowPriority = objectCandidates.stream().filter(Subject::getLOW_PRIO).collect(Collectors.toList());
            // фильтруем из списка кандидатов тех, у кого НЕ определено свойство LOW_PRIO, то есть имеют нормальный приоритет
            List<Subject> subjectsWithNormalPriority = objectCandidates.stream().filter(subject -> !subject.getLOW_PRIO()).collect(Collectors.toList());
            Subject nextObjectOwner;
            // ЕСЛИ кандидатов с нормальным приоритетом нет, то мы находим из кандидатов с низким приоритетом того
            // у кого минимальное количество объектов во владении и присваиваем следующего владельца
            if (subjectsWithNormalPriority.isEmpty()) {
                nextObjectOwner = subjectsWithLowPriority.stream().min(Comparator.comparing(Subject::ownedObjectsSize)).get();
            } else {
                // ИНАЧЕ мы находим из кандидатов с нормальным приоритетом того, у кого минимальное количество объектов во владении и присваиваем следующего владельца
                nextObjectOwner = subjectsWithNormalPriority.stream().min(Comparator.comparing(Subject::ownedObjectsSize)).get();
            }
            // отдаем объект следующему владельцу
            reassignObject(object, nextObjectOwner);
        });
    }

    /**
     * Reassign ownership of an object to another subject
     * @param object - object that changes ownership
     * @param toSubject - subject to obtain object
     */
    private void reassignObject(Integer object, Subject toSubject) {
        objectsOwners.set(object, toSubject);
        toSubject.obtainObject(object);
    }

    /**
     * @return objectsOwners
     */
    public List<Subject> getObjectsOwners() {
        return objectsOwners;
    }

    /**
     * @return subjects
     */
    public List<Subject> getSubjects() {
        return subjects;
    }
}