package piano;

public interface List {
    public Iterator createIterator();
    public void add(Object o);
    public void clear();
    public void removeLast();
    public void remove(int position);
    public Object get(int position);
    public int size();
    public String print();
}
