// Generated by view binder compiler. Do not edit!
package com.example.cwacollections.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import com.example.cwacollections.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityForgotPasswordBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final ImageButton btnBack;

  @NonNull
  public final Button btnResetPassword;

  @NonNull
  public final TextView lblHeading2;

  @NonNull
  public final TextView textView;

  @NonNull
  public final EditText txtEmail;

  private ActivityForgotPasswordBinding(@NonNull LinearLayout rootView,
      @NonNull ImageButton btnBack, @NonNull Button btnResetPassword, @NonNull TextView lblHeading2,
      @NonNull TextView textView, @NonNull EditText txtEmail) {
    this.rootView = rootView;
    this.btnBack = btnBack;
    this.btnResetPassword = btnResetPassword;
    this.lblHeading2 = lblHeading2;
    this.textView = textView;
    this.txtEmail = txtEmail;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityForgotPasswordBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityForgotPasswordBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_forgot_password, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityForgotPasswordBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnBack;
      ImageButton btnBack = rootView.findViewById(id);
      if (btnBack == null) {
        break missingId;
      }

      id = R.id.btnResetPassword;
      Button btnResetPassword = rootView.findViewById(id);
      if (btnResetPassword == null) {
        break missingId;
      }

      id = R.id.lblHeading2;
      TextView lblHeading2 = rootView.findViewById(id);
      if (lblHeading2 == null) {
        break missingId;
      }

      id = R.id.textView;
      TextView textView = rootView.findViewById(id);
      if (textView == null) {
        break missingId;
      }

      id = R.id.txtEmail;
      EditText txtEmail = rootView.findViewById(id);
      if (txtEmail == null) {
        break missingId;
      }

      return new ActivityForgotPasswordBinding((LinearLayout) rootView, btnBack, btnResetPassword,
          lblHeading2, textView, txtEmail);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}