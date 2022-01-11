package app.gigg.me.app.Activity.freelance.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hbb20.CountryCodePicker;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import app.gigg.me.app.Activity.freelance.model.PhoneContacts;
import app.gigg.me.app.Activity.freelance.ui.ShareProfileActivity;
import app.gigg.me.app.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {
    private Context context;
    private List<PhoneContacts> phoneContactsList;
    private CountryCodePicker countryCodePicker;

    public ContactsAdapter(Context context, List<PhoneContacts> phoneContactsList) {
        this.context = context;
        this.phoneContactsList = phoneContactsList;
        this.countryCodePicker = new CountryCodePicker(context);
    }

    public void update(List<PhoneContacts> phoneContactsList){
        this.phoneContactsList = phoneContactsList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contacts_item, parent, false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ContactsViewHolder holder, int position) {
        PhoneContacts contacts = phoneContactsList.get(position);
        holder.image.setImageBitmap(contacts.getImage());
        holder.name.setText(contacts.getName());
        holder.number.setText(contacts.getPhone());

        countryCodePicker.setFullNumber(contacts.getPhone());
        holder.flag.setImageResource(countryCodePicker.getSelectedCountryFlagResourceId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                // This ensures only SMS apps respond
                intent.setData(Uri.parse("smsto:"+contacts.getPhone()));
                intent.putExtra("sms_body", getMessage());
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
            }
        });
    }

    private String getMessage() {
        return "Join me on Gigg now and hire me for any job where your payment is secured till I deliver on the project.\n Download the app now! \n"
                + ShareProfileActivity.mInvitationUrl.toString();
    }

    @Override
    public int getItemCount() {
        return phoneContactsList.size();
    }


    public class ContactsViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView image;
        private TextView name;
        private TextView number;
        private ImageView flag;

        public ContactsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.iconshare3);
            name = itemView.findViewById(R.id.c_name);
            number = itemView.findViewById(R.id.c_number);
            flag = itemView.findViewById(R.id.shareicon3);
        }
    }
}
