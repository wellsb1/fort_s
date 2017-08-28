/*
 * Copyright 2008-2017 Wells Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package io.forty11.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Rows extends ArrayList<Rows.Row>
{
   RowKeys keys    = null;
   Row     lastRow = null;
   int     idx     = 0;

   public Rows()
   {
      this.keys = new RowKeys();
   }

   public Rows(String[] keys)
   {
      this.keys = new RowKeys(Arrays.asList(keys));
   }

   public Rows(List<String> keys)
   {
      this.keys = new RowKeys(keys);
   }

   public List<String> keyList()
   {
      return new ArrayList(keys.keys);
   }

   public Set<String> keySet()
   {
      return keys.keySet();
   }

   public int addKey(String key)
   {
      return keys.addKey(key);
   }

   public Row addRow()
   {
      lastRow = new Row(keys);
      add(lastRow);
      return lastRow;
   }

   public Row newRow()
   {
      return new Row(keys);
   }

   public void addRow(Object[] values)
   {
      lastRow = new Row(keys, values);
      add(lastRow);
   }

   public void put(String key, Object value)
   {
      lastRow.put(key, value);
   }

   public void put(Object value)
   {
      lastRow.add(value);
   }

   /**
    * Case insensitive map implementation
    *
    * @author Wells Burke
    */
   public class Row implements Map<String, Object>
   {
      RowKeys      keys   = null;
      List<Object> values = null;

      Row()
      {
         this.keys = new RowKeys();
         this.values = new ArrayList();
      }

      Row(RowKeys keys)
      {
         this.keys = keys;
         this.values = new ArrayList(keys.size());
      }

      Row(RowKeys keys, Object[] values)
      {
         this.keys = keys;
         this.values = new ArrayList(Arrays.asList(values));
      }

      public String getString(int index)
      {
         Object value = get(index);
         if (value != null)
            return value.toString();
         return null;
      }

      public String getString(String key)
      {
         Object value = get(key);
         if (value != null)
            return value.toString();
         return null;
      }

      public int getInt(int index)
      {
         Object value = get(index);
         if (value != null)
            return Integer.parseInt(value.toString());
         return -1;
      }

      public int getInt(String key)
      {
         Object value = get(key);
         if (value != null)
            return Integer.parseInt(value.toString());
         return -1;
      }

      public long getLong(int index)
      {
         Object value = get(index);
         if (value != null)
            return Long.parseLong(value.toString());
         return -1;
      }

      public long getLong(String key)
      {
         Object value = get(key);
         if (value != null)
            return Long.parseLong(value.toString());
         return -1;
      }

      public float getFloat(int index)
      {
         Object value = get(index);
         if (value != null)
            return Float.parseFloat(value.toString());
         return -1;
      }

      public float getFloat(String key)
      {
         Object value = get(key);
         if (value != null)
            return Float.parseFloat(value.toString());
         return -1;
      }

      public boolean getBoolean(int index)
      {
         Object value = get(index);
         if (value != null)
            return value.toString().toLowerCase().startsWith("t") || value.toString().equals("1");
         return false;
      }

      public boolean getBoolean(String key)
      {
         Object value = get(key);
         if (value != null)
            return value.toString().toLowerCase().startsWith("t") || value.toString().equals("1");
         return false;
      }

      public String toString()
      {
         StringBuffer buff = new StringBuffer("{");
         for (int i = 0; i < keys.size(); i++)
         {
            buff.append(keys.getKey(i)).append("=").append(values.get(i));
            if (i < keys.size() - 1)
               buff.append(", ");
         }
         buff.append("}");
         return buff.toString();
      }

      @Override
      public int size()
      {
         //return keys.size();
         return values.size();
      }

      @Override
      public boolean isEmpty()
      {
         return size() == 0;
      }

      @Override
      public boolean containsKey(Object key)
      {
         return indexOf((String) key) >= 0;
      }

      public int indexOf(String key)
      {
         return keys.indexOf(key);
      }

      @Override
      public boolean containsValue(Object value)
      {
         for (Object v : values)
         {
            if (v == null && value == null)
               return true;

            if (v != null && v.equals(value))
               return true;
         }
         return false;
      }

      public Object get(String key)
      {
         try
         {
            int idx = indexOf(key);
            if (idx >= 0)
               return values.get(idx);
         }
         catch (Exception ex)
         {
            System.err.println("Trying to get invalid key '" + key + "' from row.  Valide keys are " + keys.keys);
            ex.printStackTrace();
            //
            //            int idx = indexOf(key);
            //            System.out.println(idx);
            //            System.out.println(values.size());
            //
            //            System.out.println(keys.keys.size());
            //            System.out.println(keys.lc.size());
         }
         return null;
      }

      public Object get(int index)
      {
         return values.get(index);
      }

      @Override
      public Object get(Object key)
      {
         if (key == null)
            return null;

         if (key instanceof Integer)
            return values.get(((Integer) key).intValue());

         return values.get(keys.indexOf((String) key));
      }

      public void set(int index, Object value)
      {
         values.set(index, value);
      }

      public void add(Object value)
      {
         values.add(value);
      }

      @Override
      public Object put(String key, Object value)
      {
         int idx = keys.indexOf(key);
         if (idx >= 0)
         {
            while (idx > values.size() - 1)
               values.add(null);

            return values.set(idx, value);
         }
         else
         {
            keys.addKey(key);
            values.add(value);
            return value;
         }
      }

      @Override
      public Object remove(Object key)
      {
         int idx = keys.indexOf((String) key);
         if (idx >= 0)
         {
            Object value = values.get(idx);
            values.set(idx, null);
            return value;
         }
         return null;
      }

      @Override
      public void putAll(Map<? extends String, ? extends Object> m)
      {
         for (String key : m.keySet())
            put(key, m.get(key));

      }

      @Override
      public void clear()
      {
         keys = new RowKeys();
         values.clear();
      }

      @Override
      public Set<String> keySet()
      {
         return keys.keySet();
      }

      @Override
      public Collection<Object> values()
      {
         return Collections.unmodifiableList(values);
      }

      public List<Object> asList()
      {
         return Collections.unmodifiableList(values);
      }

      @Override
      public Set<Entry<String, Object>> entrySet()
      {
         LinkedHashSet<Entry<String, Object>> entries = new LinkedHashSet();
         for (int i = 0; i < keys.size(); i++)
            entries.add(new E(keys.getKey(i), values.get(i)));

         return entries;
      }

      class E implements Entry<String, Object>
      {
         String key   = null;
         Object value = null;

         public E(String key, Object value)
         {
            super();
            this.key = key;
            this.value = value;
         }

         public String getKey()
         {
            return key;
         }

         public void setKey(String key)
         {
            this.key = key;
         }

         public Object getValue()
         {
            return value;
         }

         public Object setValue(Object value)
         {
            Object v = this.value;
            this.value = value;
            return v;
         }
      }

   }

   private class RowKeys
   {
      List<String>         keys   = new ArrayList();
      Map<String, Integer> lc     = new HashMap();
      Set<String>          keySet = null;

      RowKeys()
      {
      }

      RowKeys(List<String> keys)
      {
         setKeys(keys);
      }

      int addKey(String key)
      {
         keySet = null;

         if (key == null)
            return -1;

         String lc = key.toLowerCase();
         int i = 1;
         while (keys.contains(lc))
         {
            lc = key.toLowerCase() + "_" + i;
            i++;
         }

         this.keys.add(key);
         this.lc.put(lc, this.keys.size() - 1);

         return this.keys.size();
      }

      void setKeys(List<String> keys)
      {
         keySet = null;

         this.keys.clear();
         this.lc.clear();

         for (String key : keys)
         {
            addKey(key);
         }
      }

      int indexOf(String key)
      {
         if (key == null)
            return -1;

         Integer idx = lc.get(key.toLowerCase());
         if (idx != null)
            return idx;

         return -1;
      }

      int size()
      {
         return keys.size();
      }

      String getKey(int index)
      {
         return keys.get(index);
      }

      Set<String> keySet()
      {
         if (keySet == null)
            keySet = new LinkedHashSet(this.keys);

         return keySet;
      }
   }

}
