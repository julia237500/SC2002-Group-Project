package relationship;

import java.util.List;

public interface Relationship<U, V> {
    public int delete(Object entity);
    public void add(U entity1, V entity2);
    public List<?> getRelatedEntities(Object entity);
    public boolean isRelationshipExist(U entity1, V entity2);
} 
