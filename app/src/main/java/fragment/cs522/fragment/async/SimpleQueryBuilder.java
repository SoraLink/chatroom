package fragment.cs522.fragment.async;

/**
 * Created by lizheng on 2017/2/24.
 */

import android.database.Cursor;

import java.util.List;

public class SimpleQueryBuilder implements IContinue<Cursor>{

    public interface ISimpleQueryListener<T> {

        public void handleResults(List<T> results);

    }

    // TODO Complete the implementation of this

    @Override
    public void kontinue(Cursor value) {
        // TODO complete this
    }

}
