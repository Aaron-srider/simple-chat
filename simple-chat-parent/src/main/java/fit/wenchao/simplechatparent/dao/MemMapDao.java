package fit.wenchao.simplechatparent.dao;

import fit.wenchao.simplechatparent.utils.OrikaMapperUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class MemMapDao<P, E>
{
    private final Map<P, E> dataMap = new HashMap<>();

    public abstract String getPrimaryKeyName();

    private P getPrimaryKey(E item) throws NoSuchFieldException, IllegalAccessException
    {
        Class<?> aClass = item.getClass();
        Field declaredField = aClass.getDeclaredField(getPrimaryKeyName());
        declaredField.setAccessible(true);
        return (P) declaredField.get(item);
    }


    public synchronized void add(E item)
    {
        try
        {
            P primaryKey = getPrimaryKey(item);
            dataMap.putIfAbsent(primaryKey, item);
        } catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized E get(P primaryKey)
    {
        E item = dataMap.get(primaryKey);
        return newCopy(item);
    }

    public synchronized void update(E item) {
        try
        {
            E copy = newCopy(item);
            P primaryKey = getPrimaryKey(item);
            E exists = get(primaryKey);
            if(exists != null) {
                dataMap.put(primaryKey, copy);
            }
        } catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized void remove(P pri)
    {
        dataMap.remove(pri);
    }

    private E newCopy(E item)
    {
        try
        {
            E copy = (E) item.getClass().newInstance();
            OrikaMapperUtils.map(item, copy);
            return copy;
        } catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized List<E> list()
    {
        List<E> list = new ArrayList<>();
        for (P primaryKey : dataMap.keySet())
        {
            E item = dataMap.get(primaryKey);
            E copy = newCopy(item);
            list.add(copy);
        }
        return list;
    }


}
