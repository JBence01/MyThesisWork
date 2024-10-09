package hu.unideb.inf.aic;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

// ViewModelProvider.Factory implementálása, a paraméter átadás miatt
public class SzenzorViewModelFactory implements ViewModelProvider.Factory {

    private final DBkezelo DBkezelo;

    public SzenzorViewModelFactory(DBkezelo dbkezelo) {
        this.DBkezelo = dbkezelo;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SzenzorViewModel.class)) {
            return (T) new SzenzorViewModel(DBkezelo); // Létrehozzuk a ViewModel példányt
        }
        throw new IllegalArgumentException("Ismeretlen ViewModel osztály");
    }
}
