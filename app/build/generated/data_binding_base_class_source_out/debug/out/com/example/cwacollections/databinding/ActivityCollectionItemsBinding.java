// Generated by view binder compiler. Do not edit!
package com.example.cwacollections.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.cwacollections.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityCollectionItemsBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final AppBarLayout appBarLayout;

  @NonNull
  public final FloatingActionButton btnAddItem;

  @NonNull
  public final TextView lblCollectionName;

  @NonNull
  public final TextView lblGoal;

  @NonNull
  public final TextView llNoCollection;

  @NonNull
  public final NestedScrollView nestedScrollView;

  @NonNull
  public final CoordinatorLayout relativeLayout;

  @NonNull
  public final RecyclerView rvItems;

  @NonNull
  public final ToolbarCollectionItemsBinding toolbar;

  @NonNull
  public final RelativeLayout toolbarHeading;

  private ActivityCollectionItemsBinding(@NonNull CoordinatorLayout rootView,
      @NonNull AppBarLayout appBarLayout, @NonNull FloatingActionButton btnAddItem,
      @NonNull TextView lblCollectionName, @NonNull TextView lblGoal,
      @NonNull TextView llNoCollection, @NonNull NestedScrollView nestedScrollView,
      @NonNull CoordinatorLayout relativeLayout, @NonNull RecyclerView rvItems,
      @NonNull ToolbarCollectionItemsBinding toolbar, @NonNull RelativeLayout toolbarHeading) {
    this.rootView = rootView;
    this.appBarLayout = appBarLayout;
    this.btnAddItem = btnAddItem;
    this.lblCollectionName = lblCollectionName;
    this.lblGoal = lblGoal;
    this.llNoCollection = llNoCollection;
    this.nestedScrollView = nestedScrollView;
    this.relativeLayout = relativeLayout;
    this.rvItems = rvItems;
    this.toolbar = toolbar;
    this.toolbarHeading = toolbarHeading;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityCollectionItemsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityCollectionItemsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_collection_items, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityCollectionItemsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.appBarLayout;
      AppBarLayout appBarLayout = ViewBindings.findChildViewById(rootView, id);
      if (appBarLayout == null) {
        break missingId;
      }

      id = R.id.btnAddItem;
      FloatingActionButton btnAddItem = ViewBindings.findChildViewById(rootView, id);
      if (btnAddItem == null) {
        break missingId;
      }

      id = R.id.lblCollectionName;
      TextView lblCollectionName = ViewBindings.findChildViewById(rootView, id);
      if (lblCollectionName == null) {
        break missingId;
      }

      id = R.id.lblGoal;
      TextView lblGoal = ViewBindings.findChildViewById(rootView, id);
      if (lblGoal == null) {
        break missingId;
      }

      id = R.id.llNoCollection;
      TextView llNoCollection = ViewBindings.findChildViewById(rootView, id);
      if (llNoCollection == null) {
        break missingId;
      }

      id = R.id.nestedScrollView;
      NestedScrollView nestedScrollView = ViewBindings.findChildViewById(rootView, id);
      if (nestedScrollView == null) {
        break missingId;
      }

      CoordinatorLayout relativeLayout = (CoordinatorLayout) rootView;

      id = R.id.rvItems;
      RecyclerView rvItems = ViewBindings.findChildViewById(rootView, id);
      if (rvItems == null) {
        break missingId;
      }

      id = R.id.toolbar;
      View toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }
      ToolbarCollectionItemsBinding binding_toolbar = ToolbarCollectionItemsBinding.bind(toolbar);

      id = R.id.toolbarHeading;
      RelativeLayout toolbarHeading = ViewBindings.findChildViewById(rootView, id);
      if (toolbarHeading == null) {
        break missingId;
      }

      return new ActivityCollectionItemsBinding((CoordinatorLayout) rootView, appBarLayout,
          btnAddItem, lblCollectionName, lblGoal, llNoCollection, nestedScrollView, relativeLayout,
          rvItems, binding_toolbar, toolbarHeading);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
