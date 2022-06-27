// Generated by view binder compiler. Do not edit!
package com.example.cwacollections.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.cwacollections.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityRegistrationBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final ImageButton btnShowPassword1;

  @NonNull
  public final ImageButton btnShowPassword2;

  @NonNull
  public final Button btnSignup;

  @NonNull
  public final TextView lblConfirm;

  @NonNull
  public final TextView lblEmail;

  @NonNull
  public final TextView lblHeading2;

  @NonNull
  public final TextView lblLogin;

  @NonNull
  public final TextView lblName;

  @NonNull
  public final TextView lblPassword;

  @NonNull
  public final EditText txtConfirm;

  @NonNull
  public final EditText txtName;

  @NonNull
  public final EditText txtSEmail;

  @NonNull
  public final EditText txtSPassword;

  private ActivityRegistrationBinding(@NonNull RelativeLayout rootView,
      @NonNull ImageButton btnShowPassword1, @NonNull ImageButton btnShowPassword2,
      @NonNull Button btnSignup, @NonNull TextView lblConfirm, @NonNull TextView lblEmail,
      @NonNull TextView lblHeading2, @NonNull TextView lblLogin, @NonNull TextView lblName,
      @NonNull TextView lblPassword, @NonNull EditText txtConfirm, @NonNull EditText txtName,
      @NonNull EditText txtSEmail, @NonNull EditText txtSPassword) {
    this.rootView = rootView;
    this.btnShowPassword1 = btnShowPassword1;
    this.btnShowPassword2 = btnShowPassword2;
    this.btnSignup = btnSignup;
    this.lblConfirm = lblConfirm;
    this.lblEmail = lblEmail;
    this.lblHeading2 = lblHeading2;
    this.lblLogin = lblLogin;
    this.lblName = lblName;
    this.lblPassword = lblPassword;
    this.txtConfirm = txtConfirm;
    this.txtName = txtName;
    this.txtSEmail = txtSEmail;
    this.txtSPassword = txtSPassword;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityRegistrationBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityRegistrationBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_registration, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityRegistrationBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnShowPassword1;
      ImageButton btnShowPassword1 = ViewBindings.findChildViewById(rootView, id);
      if (btnShowPassword1 == null) {
        break missingId;
      }

      id = R.id.btnShowPassword2;
      ImageButton btnShowPassword2 = ViewBindings.findChildViewById(rootView, id);
      if (btnShowPassword2 == null) {
        break missingId;
      }

      id = R.id.btnSignup;
      Button btnSignup = ViewBindings.findChildViewById(rootView, id);
      if (btnSignup == null) {
        break missingId;
      }

      id = R.id.lblConfirm;
      TextView lblConfirm = ViewBindings.findChildViewById(rootView, id);
      if (lblConfirm == null) {
        break missingId;
      }

      id = R.id.lblEmail;
      TextView lblEmail = ViewBindings.findChildViewById(rootView, id);
      if (lblEmail == null) {
        break missingId;
      }

      id = R.id.lblHeading2;
      TextView lblHeading2 = ViewBindings.findChildViewById(rootView, id);
      if (lblHeading2 == null) {
        break missingId;
      }

      id = R.id.lblLogin;
      TextView lblLogin = ViewBindings.findChildViewById(rootView, id);
      if (lblLogin == null) {
        break missingId;
      }

      id = R.id.lblName;
      TextView lblName = ViewBindings.findChildViewById(rootView, id);
      if (lblName == null) {
        break missingId;
      }

      id = R.id.lblPassword;
      TextView lblPassword = ViewBindings.findChildViewById(rootView, id);
      if (lblPassword == null) {
        break missingId;
      }

      id = R.id.txtConfirm;
      EditText txtConfirm = ViewBindings.findChildViewById(rootView, id);
      if (txtConfirm == null) {
        break missingId;
      }

      id = R.id.txtName;
      EditText txtName = ViewBindings.findChildViewById(rootView, id);
      if (txtName == null) {
        break missingId;
      }

      id = R.id.txtSEmail;
      EditText txtSEmail = ViewBindings.findChildViewById(rootView, id);
      if (txtSEmail == null) {
        break missingId;
      }

      id = R.id.txtSPassword;
      EditText txtSPassword = ViewBindings.findChildViewById(rootView, id);
      if (txtSPassword == null) {
        break missingId;
      }

      return new ActivityRegistrationBinding((RelativeLayout) rootView, btnShowPassword1,
          btnShowPassword2, btnSignup, lblConfirm, lblEmail, lblHeading2, lblLogin, lblName,
          lblPassword, txtConfirm, txtName, txtSEmail, txtSPassword);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
