package relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exception.RelationshipException;

public class OneToManyRelationship<U, V> implements Relationship<U, V>{
    private final Map<U, Set<V>> forwardMap = new HashMap<>();
    private final Map<V, U> reverseMap = new HashMap<>();

    @Override
    public void add(U one, V many){
        if(reverseMap.containsKey(many)){
            throw new RelationshipException(many.getClass() + " already has relationship.");
        }

        if(!forwardMap.containsKey(one)) forwardMap.put(one, new HashSet<V>());
        forwardMap.get(one).add(many);
        reverseMap.put(many, one);
    }

    @Override
    public List<?> getRelatedEntities(Object entity) {
        if(forwardMap.containsKey(entity)) return new ArrayList<>(forwardMap.get(entity));
        if(reverseMap.containsKey(entity)) return List.of(reverseMap.get(entity));
        
        return List.of();
    }
    
    @Override
    public int delete(Object entity) {
        if(forwardMap.containsKey(entity)){
            Set<V> values = forwardMap.remove(entity);
            for(V value:values){
                reverseMap.remove(value);
            }

            return values.size();
        }
        if(reverseMap.containsKey(entity)){
            U value = reverseMap.remove(entity);
            forwardMap.get(value).remove(entity);
            if(forwardMap.get(value).isEmpty()) forwardMap.remove(value);
            
            return 1;
        }
        
        return 0;
    }

    @Override
    public boolean isRelationshipExist(U entity1, V entity2) {
        return reverseMap.get(entity2) == entity1;
    }
}
