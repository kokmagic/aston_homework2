package mapper;

public interface Mapper<E, T> {
    E toEntity(T dto);

    T toDto(E entity);
}
