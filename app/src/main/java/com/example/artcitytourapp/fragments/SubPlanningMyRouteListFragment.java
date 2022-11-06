package com.example.artcitytourapp.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.artcitytourapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.TimeZone;

import Ruta.RutaPersonalizada;
import Sitio.SitioPersonalizado;

public class SubPlanningMyRouteListFragment extends Fragment {
    View view;
    TableLayout table;
    private AlertDialog scheduleDialog, commentDialog;

    Calendar lastDate = new GregorianCalendar();
    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_sub_planning_my_route_list, container, false);
        loadData();
        return view;
    }

    public void loadData(){
        table = view.findViewById(R.id.tableSitesList);
        table.setStretchAllColumns(true);
        table.setWeightSum(1);
        if (RutaPersonalizada.getInstance().getMyRoute().size()>0){
            // remove other buttons and labels
            TextView addSitesTextViews = view.findViewById(R.id.addSitesTextViews);
            TextView personalizeTextView = view.findViewById(R.id.personalizeTextView);
            Button exploreBtn = view.findViewById(R.id.exploreBtn);
            addSitesTextViews.setVisibility(View.GONE);
            personalizeTextView.setVisibility(View.GONE);
            exploreBtn.setVisibility(View.GONE);

            // set table and plus button



            ConstraintLayout cl = PlanningFragment.view.findViewById(R.id.botones_burbuja);

            /* todo Boton burbuja que abre el menu de botones burbuja
                vuelve visible el menu(cl) luego deshabilita
                los componentes de la ventana planear
                y por ultimo vuelve invisible el boton del +(plus)*/
            FloatingActionButton fab = view.findViewById(R.id.add_fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("prueba","prueba1");
                    cl.setVisibility(View.VISIBLE);
                    habilitar(false);
                    fab.setVisibility(View.INVISIBLE);
                }
            });

            /* todo Boton burbuja que Cierra el menu de botones burbuja
                vuelve Invisible el menu(cl) luego habilita
                los componentes de la ventana planear
                y por ultimo vuelve Visible el boton del +(plus)*/
            FloatingActionButton fabQuit = cl.findViewById(R.id.add_fab2);
            fabQuit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("prueba","prueba2");
                        cl.setVisibility(View.INVISIBLE);
                        habilitar(true);
                        fab.setVisibility(View.VISIBLE);
                    }
            });

            /* todo Boton burbuja que elimina todos los sitios de planear*/
            FloatingActionButton fabDeleteSites = cl.findViewById(R.id.add_fab3);
            fabDeleteSites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("prueba","prueba3");
                    cl.setVisibility(View.INVISIBLE);
                    habilitar(true);
                    fab.setVisibility(View.VISIBLE);
                }
            });
            /* todo Boton burbuja que agrega sitios de planear*/
            FloatingActionButton fabAddSite = cl.findViewById(R.id.add_fab1);
            fabAddSite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view).navigate(R.id.addPlanning);
                }
            });






            ArrayList<SitioPersonalizado> sites = (ArrayList<SitioPersonalizado>) RutaPersonalizada.getInstance().getMyRoute();
            for (SitioPersonalizado site : sites){
                bdGetSiteFoto(site);
            }

            // set last scheduleTime
            formatter.setTimeZone(TimeZone.getTimeZone("UTC-6"));
            lastDate.setTime(sites.get(sites.size()-1).getHoraVisita().toDate());
        }
        else{
            table.setVisibility(View.GONE);
            FloatingActionButton optionsBtn = view.findViewById(R.id.add_fab);
            optionsBtn.setVisibility(View.GONE);

            Button exploreBtn = view.findViewById(R.id.exploreBtn);
            exploreBtn.setOnClickListener(view -> {
                Navigation.findNavController(view).navigate(R.id.addPlanning);
            });
        }
    }
    protected void habilitar(boolean opc){
        PlanningFragment.view.findViewById(R.id.myRouteLbl).setEnabled(opc);
        PlanningFragment.view.findViewById(R.id.favLbl).setEnabled(opc);
        PlanningFragment.view.findViewById(R.id.planning_share_button).setEnabled(opc);
        PlanningFragment.view.findViewById(R.id.planning_notification_button).setEnabled(opc);
        View v = PlanningFragment.view.findViewById(R.id.planningContainer);
        v.findViewById(R.id.orderSwitch).setEnabled(opc);
        v.findViewById(R.id.listSwitch).setEnabled(opc);
        v.findViewById(R.id.editPlanTitle).setEnabled(opc);
    }

    protected void addTableRow(SitioPersonalizado site, String imgPath) {
        TableRow siteRow = (TableRow)LayoutInflater.from(getContext()).inflate(R.layout.planning_row, null);

        ImageView siteImageView = siteRow.findViewById(R.id.siteImageView);
        TextView siteScheduleTextView = siteRow.findViewById(R.id.siteScheduleTextView);
        TextView siteTextView = siteRow.findViewById(R.id.siteTextView);
        TextView siteTypeTextView = siteRow.findViewById(R.id.siteTypeTextView);
        TextView editTextComment = siteRow.findViewById(R.id.editTextComment);
        ImageView removeBtn = siteRow.findViewById(R.id.removeBtn);

        imageRow(siteImageView, imgPath);
        siteTextView.setText(site.getNombre());
        siteTypeTextView.setText(site.getTipoSitio());
        editTextComment.setText(site.getComentario());

        if (site.getHoraVisita().toDate().compareTo(new Date()) < 0)
            siteScheduleTextView.setText(R.string.add_schedule);
        else
            siteScheduleTextView.setText(formatter.format(site.getHoraVisita().toDate()));

        siteScheduleTextView.setOnClickListener(view -> createScheduleDialog(site, siteScheduleTextView));

        editTextComment.setOnClickListener(view -> createCommentDialog(site, editTextComment));

        removeBtn.setClickable(true);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RutaPersonalizada.getInstance().removeSiteMyRouteList(site, view);
            }
        });
        //removeBtn.setOnClickListener(view -> RutaPersonalizada.getInstance().removeSiteMyRouteList(site, view));

        table.addView(siteRow);
    }

    protected void bdGetSiteFoto(SitioPersonalizado site){
        if (site.getIdFotoPredeterminada() == null){
            addTableRow(site, "Imagenes Interfaz/notFoundImage.png");
        }
        else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Fotografia").document(site.getIdFotoPredeterminada());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.get("foto") == null) {
                            addTableRow(site, "Imagenes Interfaz/notFoundImage.png");
                        } else {
                            addTableRow(site, (String) Objects.requireNonNull(document.get("foto")));
                        }
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            });
        }
    }

    protected void imageRow(ImageView iv, String imgPath){
        StorageReference pathReference  = FirebaseStorage.getInstance().getReference(imgPath);
        try {
            File localFile = File.createTempFile("tempFile", imgPath.substring(imgPath.lastIndexOf(".")));
            pathReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                iv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 180, 180, false));
            });
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    //dialogs
    protected void emptyValues(){
        new AlertDialog.Builder(requireContext())
                .setTitle("Debe rellenar todos los campos")
                .setMessage("El comentario del sitio esta vacias")
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    protected void noDate(){
        new AlertDialog.Builder(requireContext())
                .setTitle("Debe seleccionar todos los campos")
                .setMessage("Debe seleccionar un dia y hora diferentes a este momento")
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    void createScheduleDialog(SitioPersonalizado site, TextView siteScheduleTextView){
        AlertDialog.Builder scheduleDialogBuilder = new AlertDialog.Builder(requireContext());
        final View schedulePopupWindow = getLayoutInflater().inflate(R.layout.datepicker_popup, null);

        DatePicker datePicker = schedulePopupWindow.findViewById(R.id.datePicker);
        TimePicker timePicker = schedulePopupWindow.findViewById(R.id.timePicker);
        Button sendBtn = schedulePopupWindow.findViewById(R.id.sendBtn);
        TextView cleanTextView =  schedulePopupWindow.findViewById(R.id.cleanTextView);

        datePicker.init(lastDate.get(Calendar.YEAR), lastDate.get(Calendar.MONTH), lastDate.get(Calendar.DAY_OF_MONTH), null);
        timePicker.setCurrentHour(lastDate.get(Calendar.HOUR));
        timePicker.setCurrentMinute(lastDate.get(Calendar.MINUTE));

        scheduleDialogBuilder.setView(schedulePopupWindow);
        scheduleDialog = scheduleDialogBuilder.create();
        scheduleDialog.show();

        sendBtn.setOnClickListener(view -> {
            Date today = new Date();
            Date selectedDate = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute()).getTime();
            if (today.compareTo(selectedDate) < 0){
                lastDate.setTime(selectedDate);
                site.updateScheduleBd(selectedDate, view, siteScheduleTextView);
                scheduleDialog.hide();
            }
            else{
                noDate();
            }
        });

        cleanTextView.setOnClickListener(view -> scheduleDialog.hide());
    }

    void createCommentDialog(SitioPersonalizado site, TextView editTextComment){
        AlertDialog.Builder commentDialogBuilder = new AlertDialog.Builder(requireContext());
        final View commentPopupWindow = getLayoutInflater().inflate(R.layout.comment_popup, null);

        EditText commentText = commentPopupWindow.findViewById(R.id.commentText);
        Button sendBtn = commentPopupWindow.findViewById(R.id.sendBtn);
        TextView cleanTextView = commentPopupWindow.findViewById(R.id.cleanTextView);

        commentText.setText(site.getComentario());

        commentDialogBuilder.setView(commentPopupWindow);
        commentDialog = commentDialogBuilder.create();
        commentDialog.show();

        sendBtn.setOnClickListener(view -> {
            if (!commentText.getText().toString().isEmpty()){
                site.updateCommentBd(commentText.getText().toString(), view, editTextComment);
                commentDialog.hide();
            }
            else{
                emptyValues();
            }
        });

        cleanTextView.setOnClickListener(view -> commentDialog.hide());
    }
}