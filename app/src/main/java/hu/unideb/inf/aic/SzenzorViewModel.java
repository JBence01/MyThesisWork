package hu.unideb.inf.aic;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class SzenzorViewModel extends ViewModel {

    private LiveData<SzenzorAdat> szenzorAdat;
    private DBkezelo DBkezelo;

    public SzenzorViewModel(DBkezelo DBkezelo) {
        this.DBkezelo = DBkezelo;
        szenzorAdat = DBkezelo.adatOlvas();
    }

    public LiveData<SzenzorAdat> getSzenzorAdat() {
        return szenzorAdat;
    }

    public LiveData<List<SzenzorAdat>> adatOlvas30() {
        return DBkezelo.adatOlvas30();
    }
}
