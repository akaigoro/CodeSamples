package so.generic.dogfinder;

import jdk.nashorn.internal.runtime.options.Option;

public abstract class AbstractFinderClass<T extends Animal & Canine<T>> {

    Repository repository;
    private Class<T> animalType;

    public AbstractFinderClass(Class<T> animalType) {
        this.animalType = animalType;
    }

    public Option<T> findMyAnimal(String id) {
        return repository.findById(id, this.animalType);
    }
}
