package so.generic.dogfinder;

import jdk.nashorn.internal.runtime.options.Option;

interface Repository {
    <T extends Animal> Option<T> findById(String id, Class<T> type);
}