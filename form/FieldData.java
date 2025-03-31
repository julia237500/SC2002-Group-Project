package form;

public class FieldData<T> {
    private T data;

    public FieldData(T data){
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
