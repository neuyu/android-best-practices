package com.neu.contact.contactui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.neu.contact.R;


/**
 * Created by neuyuandaima on 2015/12/31.
 */
public final class ContactsPickerActivity extends AppCompatActivity implements OnContactSelectedListener {
    public static final String SELECTED_CONTACT_ID = "contact_id";
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_CONTACT_NAME = "contact_name";

    /**
     * Starting point
     * Loads the {@link ContactsListFragment}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_activity_contacts);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ContactsListFragment fragment = new ContactsListFragment();

        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Select contact");
        }
    }

    /**
     * Callback when the contact is selected from the list of contacts.
     * Loads the {@link ContactDetailsFragment}
     */
    @Override
    public void onContactNameSelected(long contactId) {
        /* Now that we know which Contact was selected we can go to the details fragment */

        Fragment detailsFragment = new ContactDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ContactsPickerActivity.SELECTED_CONTACT_ID, contactId);
        detailsFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, detailsFragment);

        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    /**
     * Callback when the contact number is selected from the contact details view
     * Sets the activity result with the contact information and finishes
     */
    @Override
    public void onContactNumberSelected(String contactNumber, String contactName) {
        Intent intent = new Intent();
        intent.putExtra(KEY_PHONE_NUMBER, contactNumber);
        intent.putExtra(KEY_CONTACT_NAME, contactName);
        setResult(RESULT_OK, intent);
        finish();
    }


}
