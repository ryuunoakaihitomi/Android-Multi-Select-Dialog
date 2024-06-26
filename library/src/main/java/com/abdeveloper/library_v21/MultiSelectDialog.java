package com.abdeveloper.library_v21;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.SearchView;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

public class MultiSelectDialog extends AppCompatDialogFragment implements SearchView.OnQueryTextListener, View.OnClickListener {

    public static ArrayList<Integer> selectedIdsForCallback = new ArrayList<>();

    public ArrayList<MultiSelectModel> mainListOfAdapter = new ArrayList<>();
    private MultiSelectAdapter multiSelectAdapter;
    //Default Values
    private String title;
    private float titleSize = 25;
    private String positiveText;
    private String negativeText;
    private String neutralText;
    private TextView dialogTitle, dialogSubmit, dialogCancel, dialogNeutral;
    private ArrayList<Integer> previouslySelectedIdsList = new ArrayList<>();


    private ArrayList<Integer> tempPreviouslySelectedIdsList = new ArrayList<>();
    private ArrayList<MultiSelectModel> tempMainListOfAdapter = new ArrayList<>();

    private SubmitCallbackListener submitCallbackListener;
    private @Nullable Consumer<Dialog> dialogInterceptor;
    private @Nullable Consumer<DialogInterface> dialogDismissListener;

    private int minSelectionLimit = 1;
    private String minSelectionMessage = null;
    private int maxSelectionLimit = 0;
    private String maxSelectionMessage = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.setContentView(R.layout.msd_custom_multi_select);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        RecyclerViewEmptySupport recyclerView = dialog.findViewById(R.id.recycler_view);
        SearchView searchView = dialog.findViewById(R.id.search_view);
        dialogTitle = dialog.findViewById(R.id.title);
        dialogSubmit = dialog.findViewById(R.id.done);
        dialogCancel = dialog.findViewById(R.id.cancel);
        dialogNeutral = dialog.findViewById(R.id.neutral);

        recyclerView.setEmptyView(dialog.findViewById(R.id.list_empty1));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        dialogSubmit.setOnClickListener(this);
        dialogCancel.setOnClickListener(this);
        if (neutralText != null) {
            dialogNeutral.setOnClickListener(this);
        }

        settingValues();

        mainListOfAdapter = setCheckedIDS(mainListOfAdapter, previouslySelectedIdsList);
        multiSelectAdapter = new MultiSelectAdapter(mainListOfAdapter, getContext());
        recyclerView.setAdapter(multiSelectAdapter);

        searchView.setOnQueryTextListener(this);
        searchView.onActionViewExpanded();
        searchView.clearFocus();

        if (dialogInterceptor != null) {
            dialogInterceptor.accept(dialog);
        }

        return dialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dialogDismissListener != null) {
            dialogDismissListener.accept(dialog);
        }
    }

    public MultiSelectDialog title(String title) {
        this.title = title;
        return this;
    }

    public MultiSelectDialog titleSize(float titleSize) {
        this.titleSize = titleSize;
        return this;
    }

    public MultiSelectDialog positiveText(@NonNull String message) {
        this.positiveText = message;
        return this;
    }

    public MultiSelectDialog negativeText(@NonNull String message) {
        this.negativeText = message;
        return this;
    }

    /**
     * If the text is not set, the neutral button will not be enabled,
     * {@link SubmitCallbackListener#onNeutral(ArrayList, ArrayList, String)} will not be called
     *
     * @param message the message
     * @return this object
     */
    public MultiSelectDialog neutralText(@NonNull String message) {
        this.neutralText = message;
        return this;
    }

    public MultiSelectDialog preSelectIDsList(ArrayList<Integer> list) {
        this.previouslySelectedIdsList = list;
        this.tempPreviouslySelectedIdsList = new ArrayList<>(previouslySelectedIdsList);
        return this;
    }

    public MultiSelectDialog multiSelectList(ArrayList<MultiSelectModel> list) {
        this.mainListOfAdapter = list;
        this.tempMainListOfAdapter = new ArrayList<>(mainListOfAdapter);
        if (maxSelectionLimit == 0)
            maxSelectionLimit = list.size();
        return this;
    }

    public MultiSelectDialog setMaxSelectionLimit(int limit) {
        this.maxSelectionLimit = limit;
        return this;
    }

    public MultiSelectDialog setMaxSelectionMessage(String message) {
        this.maxSelectionMessage = message;
        return this;
    }

    public MultiSelectDialog setMinSelectionLimit(int limit) {
        this.minSelectionLimit = limit;
        return this;
    }

    public MultiSelectDialog setMinSelectionMessage(String message) {
        this.minSelectionMessage = message;
        return this;
    }

    public MultiSelectDialog onSubmit(@NonNull SubmitCallbackListener callback) {
        this.submitCallbackListener = callback;
        return this;
    }

    /**
     * Used to customize the dialog
     *
     * @param consumer The callback contains the {@link Dialog}
     * @return this {@link MultiSelectDialog}
     * @see AppCompatDialogFragment#onCreateDialog(Bundle)
     */
    public MultiSelectDialog dialogInterceptor(@NonNull Consumer<Dialog> consumer) {
        this.dialogInterceptor = consumer;
        return this;
    }

    /**
     * @param dismissListener {@link Runnable}
     * @return this object
     * @see #onDismiss(DialogInterface)
     */
    public MultiSelectDialog dismissListener(@NonNull Consumer<DialogInterface> dismissListener) {
        dialogDismissListener = dismissListener;
        return this;
    }

    private void settingValues() {
        dialogTitle.setText(title);
        dialogTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize);
        if (positiveText == null) {
            positiveText = getString(android.R.string.ok);
        }
        if (negativeText == null) {
            negativeText = getString(android.R.string.cancel);
        }
        dialogSubmit.setText(positiveText);
        dialogNeutral.setText(neutralText);
        dialogCancel.setText(negativeText);
    }

    private ArrayList<MultiSelectModel> setCheckedIDS(ArrayList<MultiSelectModel> multiselectdata, ArrayList<Integer> listOfIdsSelected) {
        for (int i = 0; i < multiselectdata.size(); i++) {
            multiselectdata.get(i).setSelected(false);
            for (int j = 0; j < listOfIdsSelected.size(); j++) {
                if (listOfIdsSelected.get(j) == (multiselectdata.get(i).getId())) {
                    multiselectdata.get(i).setSelected(true);
                }
            }
        }
        return multiselectdata;
    }

    private ArrayList<MultiSelectModel> filter(ArrayList<MultiSelectModel> models, String query) {
        query = query.toLowerCase();
        final ArrayList<MultiSelectModel> filteredModelList = new ArrayList<>();
        if (query.equals("") | query.isEmpty()) {
            filteredModelList.addAll(models);
            return filteredModelList;
        }

        for (MultiSelectModel model : models) {
            final String name = model.getName().toLowerCase();
            if (name.contains(query)) {
                filteredModelList.add(model);
            }
        }


        return filteredModelList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        selectedIdsForCallback = previouslySelectedIdsList;
        mainListOfAdapter = setCheckedIDS(mainListOfAdapter, selectedIdsForCallback);
        ArrayList<MultiSelectModel> filteredlist = filter(mainListOfAdapter, newText);
        multiSelectAdapter.setData(filteredlist, newText.toLowerCase(), multiSelectAdapter);
        return false;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.done) {
            ArrayList<Integer> callBackListOfIds = selectedIdsForCallback;

            if (callBackListOfIds.size() >= minSelectionLimit) {
                if (callBackListOfIds.size() <= maxSelectionLimit) {

                    //to remember last selected ids which were successfully done
                    tempPreviouslySelectedIdsList = new ArrayList<>(callBackListOfIds);

                    if (submitCallbackListener != null) {
                        submitCallbackListener.onSelected(callBackListOfIds, getSelectNameList(), getSelectedDataString());
                    }
                    dismiss();
                } else {
                    String youCan = getResources().getString(R.string.you_can_only_select_upto);
                    String options = getResources().getString(R.string.options);
                    String option = getResources().getString(R.string.option);
                    String message = "";

                    if (this.maxSelectionMessage != null) {
                        message = maxSelectionMessage;
                    } else {
                        if (maxSelectionLimit > 1)
                            message = youCan + " " + maxSelectionLimit + " " + options;
                        else
                            message = youCan + " " + maxSelectionLimit + " " + option;
                    }
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
            } else {
                String pleaseSelect = getResources().getString(R.string.please_select_atleast);
                String options = getResources().getString(R.string.options);
                String option = getResources().getString(R.string.option);
                String message = "";

                if (this.minSelectionMessage != null) {
                    message = minSelectionMessage;
                } else {
                    if (minSelectionLimit > 1)
                        message = pleaseSelect + " " + minSelectionLimit + " " + options;
                    else
                        message = pleaseSelect + " " + minSelectionLimit + " " + option;
                }
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }

        if (view.getId() == R.id.neutral) {
            if (submitCallbackListener != null) {
                submitCallbackListener.onNeutral(selectedIdsForCallback, getSelectNameList(), getSelectedDataString());
            }
            dismiss();
        }

        if (view.getId() == R.id.cancel) {
            if (submitCallbackListener != null) {
                selectedIdsForCallback.clear();
                selectedIdsForCallback.addAll(tempPreviouslySelectedIdsList);
                submitCallbackListener.onCancel();
            }
            dismiss();
        }
    }

    private String getSelectedDataString() {
        String data = "";
        for (int i = 0; i < tempMainListOfAdapter.size(); i++) {
            if (checkForSelection(tempMainListOfAdapter.get(i).getId())) {
                data = data + ", " + tempMainListOfAdapter.get(i).getName();
            }
        }
        if (data.length() > 0) {
            return data.substring(1);
        } else {
            return "";
        }
    }

    private ArrayList<String> getSelectNameList() {
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < tempMainListOfAdapter.size(); i++) {
            if (checkForSelection(tempMainListOfAdapter.get(i).getId())) {
                names.add(tempMainListOfAdapter.get(i).getName());
            }
        }
        return names;
    }

    private boolean checkForSelection(Integer id) {
        for (int i = 0; i < MultiSelectDialog.selectedIdsForCallback.size(); i++) {
            if (id.equals(MultiSelectDialog.selectedIdsForCallback.get(i))) {
                return true;
            }
        }
        return false;
    }

   /* public void setCallbackListener(SubmitCallbackListener submitCallbackListener) {
        this.submitCallbackListener = submitCallbackListener;
    }*/

    public interface SubmitCallbackListener {
        void onSelected(ArrayList<Integer> selectedIds, ArrayList<String> selectedNames, String commonSeparatedData);

        /**
         * Like {@link #onSelected(ArrayList, ArrayList, String)}, but don't check the range
         * <p>
         * {@link #setMinSelectionLimit(int)} and {@link #setMaxSelectionLimit(int)} has no effect in this case
         *
         * @see #neutralText(String)
         */
        void onNeutral(ArrayList<Integer> selectedIds, ArrayList<String> selectedNames, String commonSeparatedData);

        void onCancel();
    }

}
