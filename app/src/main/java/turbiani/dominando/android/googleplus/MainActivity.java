package turbiani.dominando.android.googleplus;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import turbiani.dominando.android.googleplus.interfaces.GoogleApiProvider;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiProvider {

    public static final int REQUEST_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mOpcaoSelecionada;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        mGoogleApiClient.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://turbiani.dominando.android.googleplus/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://turbiani.dominando.android.googleplus/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selecionarOpcaoMenu(menuItem);
                return true;
            }
        });
        if (savedInstanceState == null) {
            //AQUI EU FORÇO QUE AO INICIAR A APP O ITEM ABA JÁ VENHA SELECIONADO
            mOpcaoSelecionada = R.id.action_aba;
        } else {
            mOpcaoSelecionada = savedInstanceState.getInt("menuItem");
        }
        selecionarOpcaoMenu(mNavigationView.getMenu().findItem(mOpcaoSelecionada));

        //INICIALIZA GOOGLE API CLIENT
        initGoogleApi();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("menuItem", mOpcaoSelecionada);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selecionarOpcaoMenu(MenuItem menuItem) {
        mOpcaoSelecionada = menuItem.getItemId();

        //TRATANDO O CLIQUE NO LOGIN-LOGOUT
        if (mOpcaoSelecionada == R.id.action_login_logout) {
            if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
                mSignInClicked = true;
                login();
            } else if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            }
            return;
        }

        //COMPORTAMENTO PADRÃO DAS TRODAS DE DRAWER
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();

        String titulo = menuItem.getTitle().toString();

        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentByTag(titulo) == null) {
            Fragment primeiroNivelFragment;
            if(mOpcaoSelecionada == R.id.action_friends){
                primeiroNivelFragment = new ListaAmigosFragment();
            }else{
                primeiroNivelFragment = PrimeiroNivelFragment.novaInstancia(titulo);
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.conteudo, primeiroNivelFragment, titulo)
                    .commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == REQUEST_SIGN_IN) {
            if (responseCode != Activity.RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    // TRATAMENTO DOS CALLBACKS COM O GOOGLE PLUS

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        mIntentInProgress = false;
        atualizarMenu();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIntentInProgress) {
            mConnectionResult = connectionResult;
            if (mSignInClicked) {
                login();
            }
        }
        atualizarMenu();
    }

    private void initGoogleApi() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
    }

    private void login() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, REQUEST_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void atualizarMenu() {
        final ImageView imgCapa = (ImageView) findViewById(R.id.imgCapa);
        final ImageView imgFoto = (ImageView) findViewById(R.id.imgFotoPerfil);
        final TextView txtNome = (TextView) findViewById(R.id.txtNome);
        if (mGoogleApiClient.isConnected()) {
            Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            if (person != null) {
                txtNome.setText(person.getDisplayName()==null || person.getDisplayName()=="" ? "GooglePlus + Navegacao" : person.getDisplayName());
                if (person.hasImage()) {
                    Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                            RoundedBitmapDrawable fotoRedonda = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                            fotoRedonda.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                            imgFoto.setImageDrawable(fotoRedonda);
                        }

                        @Override
                        public void onBitmapFailed(Drawable drawable) {
                        }

                        @Override
                        public void onPrepareLoad(Drawable drawable) {
                        }
                    };
                    Picasso.with(MainActivity.this)
                            .load(person.getImage().getUrl())
                            .into(target);
                }
                if (person.hasCover()) {
                    Picasso.with(MainActivity.this)
                            .load(person.getCover().getCoverPhoto().getUrl())
                            .into(imgCapa);
                }
            }
        } else {
            imgCapa.setImageBitmap(null);
            txtNome.setText(R.string.app_name);
            imgFoto.setImageResource(R.mipmap.ic_launcher);
        }
        mNavigationView.getMenu()
                .findItem(R.id.action_login_logout)
                .setTitle(mGoogleApiClient.isConnected() ?
                        R.string.opcao_logout : R.string.opcao_login);
    }

}
