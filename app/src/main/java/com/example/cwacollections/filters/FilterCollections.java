package com.example.cwacollections.filters;

import android.widget.Filter;

import com.example.cwacollections.adapters.AdapterCollection;
import com.example.cwacollections.adapters.AdapterItem;
import com.example.cwacollections.models.ModelCollection;
import com.example.cwacollections.models.ModelItem;

import java.util.ArrayList;

public class FilterCollections extends Filter {
    //arraylist in which we want to search
    ArrayList<ModelCollection> filterList;
    //adapter in which filter needs to be implemented
    AdapterCollection adapterCollection;

    //constructor
    public FilterCollections(ArrayList<ModelCollection> filterList, AdapterCollection adapterCollection) {
        this.filterList = filterList;
        this.adapterCollection = adapterCollection;
    }
    
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //value should not be null and empty
        if(constraint != null && constraint.length() > 0) {
            //change to upper case or lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelCollection> filteredModels = new ArrayList<>();

            for(int i=0; i<filterList.size(); i++){
                //validate
                if (filterList.get(i).getCollection().toUpperCase().contains(constraint)){
                    //add to filtered list
                    filteredModels.add(filterList.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //apply filter
        adapterCollection.collectionArrayList = (ArrayList<ModelCollection>)results.values;

        //notify changes
        adapterCollection.notifyDataSetChanged();
    }
}

/* References
Book App Firebase | 04 Show Book Categories | Android Studio | Java. 2021. YouTube video, added by Atif Pervaiz
[ONLINE]. Available at: https://youtu.be/j6GrP2MdFos. [Accessed 13 July 2021].

Book App Firebase | 06 Show Books Admin | Android Studio | Java. 2021. YouTube video, added by Atif Pervaiz
[ONLINE]. Available at: https://youtu.be/vgWihyzAv-U. [Accessed 14 July 2021].

*/
