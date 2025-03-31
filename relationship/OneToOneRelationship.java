package relationship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.RelationshipException;

public class OneToOneRelationship<U, V> implements Relationship<U, V>{
    private final Map<U, V> forwardMap = new HashMap<>();
    private final Map<V, U> reverseMap = new HashMap<>();

    @Override
    public void add(U entity1, V entity2){
        if(forwardMap.containsKey(entity1)){
            throw new RelationshipException(entity1.getClass() + " already has relationship.");
        }
        if(reverseMap.containsKey(entity2)){
            throw new RelationshipException(entity2.getClass() + " already has relationship.");
        }

        forwardMap.put(entity1, entity2);
        reverseMap.put(entity2, entity1);
    }

    @Override
    public List<?> getRelatedEntities(Object entity) {
        if(forwardMap.containsKey(entity)) return List.of(forwardMap.get(entity));
        if(reverseMap.containsKey(entity)) return List.of(reverseMap.get(entity));
        
        return List.of();
    }
    
    @Override
    public int delete(Object entity) {
        if(forwardMap.containsKey(entity)){
            V value = forwardMap.remove(entity);
            reverseMap.remove(value);

            return 1;
        }
        if(reverseMap.containsKey(entity)){
            U value = reverseMap.remove(entity);
            forwardMap.remove(value);
            
            return 1;
        }
        
        return 0;
    }

    @Override
    public boolean isRelationshipExist(U entity1, V entity2) {
        return forwardMap.get(entity1) == entity2;
    }
}
