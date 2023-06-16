package nl.han.ica.datastructures;

import java.util.HashMap;

public class ScopeTable<K, V> {
    private IHANLinkedList<HashMap<K,V>> scopes = new HanLinkedList<>();

    public void openScope(){
        this.scopes.addFirst(new HashMap<>());
    }

    public void closeScope(){
        this.scopes.removeFirst();
    }

    public void addVariableToScope(K key, V value){
        this.scopes.getFirst().put(key, value);
    }

    public V getVariableValue(K key){
        for(int i = 0; i < scopes.getSize(); i++){
            HashMap<K, V> currentScope = scopes.get(i);
            V value = currentScope.get(key);
            if(value != null){
                return value;
            }
        }
        return null;
    }
}
