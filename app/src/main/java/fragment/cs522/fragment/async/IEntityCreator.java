package fragment.cs522.fragment.async;


import android.database.Cursor;

public interface IEntityCreator<T> {

    public T create(Cursor cursor);

}