package turbiani.dominando.android.googleplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import turbiani.dominando.android.googleplus.R;
import turbiani.dominando.android.googleplus.entity.Pessoa;

/**
 * Created by turbiani on 31/05/16.
 */
public class PessoaAdapter extends ArrayAdapter<Pessoa> {
    public PessoaAdapter(Context context, List<Pessoa> objects) {
        super(context, 0, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.pessoa_item_list, null);
        }
        Pessoa person = getItem(position);
        ImageView img = (ImageView)convertView.findViewById(R.id.imageView);
        TextView txt = (TextView)convertView.findViewById(R.id.textView);
        txt.setText(person.nome);
        Picasso.with(getContext()).load(person.urlFoto).into(img);
        return convertView;
    }
}
