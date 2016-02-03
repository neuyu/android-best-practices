package com.neu.contact.contactui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.neu.contact.R;

import java.lang.ref.WeakReference;

/**
 * Created by neuyuandaima on 2015/12/31.
 */
public final class ContactsListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    @SuppressLint("InlinedApi")
    private static String DISPLAY_NAME_COMPAT = Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.HONEYCOMB ?
            Contacts.DISPLAY_NAME_PRIMARY :
            Contacts.DISPLAY_NAME;
    private static final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{
            Contacts._ID,
            DISPLAY_NAME_COMPAT,
            Contacts.HAS_PHONE_NUMBER,
            Contacts.LOOKUP_KEY
    };
    private OnContactSelectedListener mContactsListener;
    private SimpleCursorAdapter mAdapter;
    private String mSearchString = null;
    private SearchView mSearchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.library_fragment_contacts_list, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        String phoneNumber = viewHolder.phoneNumber.getText().toString();
        String name = viewHolder.contactName.getText().toString();

        if (phoneNumber.equals(getString(R.string.label_multiple_numbers))) {
            mContactsListener.onContactNameSelected(id);
        } else {
            mContactsListener.onContactNumberSelected(phoneNumber, name);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mContactsListener = (OnContactSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnContactSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(0, null, this);

        mAdapter = new IndexedListAdapter(
                this.getActivity(),
                R.layout.library_list_item_contacts,
                null,
                new String[]{Contacts.DISPLAY_NAME},
                new int[]{R.id.display_name});

        setListAdapter(mAdapter);
        getListView().setFastScrollEnabled(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.library_main, menu);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int actionId = item.getItemId();
        if (actionId == R.id.action_add_contact) {
            Intent intent = new Intent(Intent.ACTION_INSERT,
                    Contacts.CONTENT_URI);
            startActivity(intent);
            return true;
        }

        if (actionId == android.R.id.home) {
            getActivity().finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Uri baseUri;

        if (mSearchString != null) {
            baseUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI,
                    Uri.encode(mSearchString));
        } else {
            baseUri = Contacts.CONTENT_URI;
        }

        String selection = "((" + DISPLAY_NAME_COMPAT + " NOTNULL) AND ("
                + Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                + DISPLAY_NAME_COMPAT + " != '' ))";

        String sortOrder = Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        return new CursorLoader(getActivity(), baseUri, CONTACTS_SUMMARY_PROJECTION, selection, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public boolean onClose() {
        if (!TextUtils.isEmpty(mSearchView.getQuery())) {
            mSearchView.setQuery(null, true);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

        if (mSearchString == null && newFilter == null) {
            return true;
        }
        if (mSearchString != null && mSearchString.equals(newFilter)) {
            return true;
        }
        mSearchString = newFilter;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    static class ViewHolder {
        TextView contactName;
        TextView phoneLabel;
        TextView phoneNumber;
        View separator;
        PhoneNumberLookupTask phoneNumberLookupTask;
    }

    class IndexedListAdapter extends SimpleCursorAdapter implements SectionIndexer {

        AlphabetIndexer alphaIndexer;

        public IndexedListAdapter(Context context, int layout, Cursor c,
                                  String[] from, int[] to) {
            super(context, layout, c, from, to, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater(null);
                convertView = inflater.inflate(R.layout.library_list_item_contacts, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.contactName = (TextView) convertView.findViewById(R.id.display_name);
                viewHolder.phoneLabel = (TextView) convertView.findViewById(R.id.phone_label);
                viewHolder.phoneNumber = (TextView) convertView.findViewById(R.id.phone_number);
                viewHolder.separator = convertView.findViewById(R.id.label_separator);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                viewHolder.phoneNumberLookupTask.cancel(true);
            }

            return super.getView(position, convertView, parent);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);

            long contactId = cursor.getLong(cursor.getColumnIndexOrThrow(Contacts._ID));
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            viewHolder.phoneNumberLookupTask = new PhoneNumberLookupTask(view);
            viewHolder.phoneNumberLookupTask.execute(contactId);
        }

        @Override
        public Cursor swapCursor(Cursor c) {
            if (c != null&&c.getCount()>0) {
                alphaIndexer = new AlphabetIndexer(c,
                        c.getColumnIndex(Contacts.DISPLAY_NAME),
                        " ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            }
            return super.swapCursor(c);
        }

        @Override
        public Object[] getSections() {
            return alphaIndexer == null ? null : alphaIndexer.getSections();
        }

        @Override
        public int getPositionForSection(int section) {
            return alphaIndexer.getPositionForSection(section);
        }

        @Override
        public int getSectionForPosition(int position) {
            return alphaIndexer.getSectionForPosition(position);
        }
    }

    /**
     * Task for looking up the phone number and displaying it next to the contact.
     * This task holds a weak reference to the view so that if it is recycled while task is running,
     * then the task does nothing.
     */
    private class PhoneNumberLookupTask extends AsyncTask<Long, Void, Void> {
        final WeakReference<View> mViewReference;

        String mPhoneNumber;
        String mPhoneLabel;

        public PhoneNumberLookupTask(View view) {
            mViewReference = new WeakReference<>(view);
        }

        @Override
        protected Void doInBackground(Long... ids) {
            String[] projection = new String[]{Phone.DISPLAY_NAME, Phone.TYPE, Phone.NUMBER, Phone.LABEL};
            long contactId = ids[0];

            final Cursor phoneCursor = getActivity().getContentResolver().query(
                    Phone.CONTENT_URI,
                    projection,
                    Data.CONTACT_ID + "=?",
                    new String[]{String.valueOf(contactId)},
                    null);

            if (phoneCursor != null && phoneCursor.moveToFirst() && phoneCursor.getCount() == 1) {
                final int contactNumberColumnIndex = phoneCursor.getColumnIndex(Phone.NUMBER);
                mPhoneNumber = phoneCursor.getString(contactNumberColumnIndex);
                int type = phoneCursor.getInt(phoneCursor.getColumnIndexOrThrow(Phone.TYPE));
                mPhoneLabel = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.LABEL));
                mPhoneLabel = Phone.getTypeLabel(getResources(), type, mPhoneLabel).toString();
                phoneCursor.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            View view = mViewReference.get();
            if (view != null) {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                if (mPhoneNumber != null) {
                    viewHolder.phoneNumber.setText(mPhoneNumber);
                    viewHolder.phoneLabel.setText(mPhoneLabel);
                    viewHolder.phoneLabel.setVisibility(View.VISIBLE);
                    viewHolder.separator.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.phoneNumber.setText(getString(R.string.label_multiple_numbers));
                    viewHolder.phoneLabel.setVisibility(View.GONE);
                    viewHolder.separator.setVisibility(View.GONE);
                }

            }
        }
    }
}
