package eg.gov.iti.jets.weatherapp.network

import eg.gov.iti.jets.weatherapp.model.Root

sealed class ApiState{
    class Success(val data:Root):ApiState()
    class Failure(val msg:Throwable):ApiState()
    object Loading:ApiState()
}
