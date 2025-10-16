package Controller;

import data.AppContainer;
import data.DefaultAppContainer;

public abstract class BaseController {
    protected AppContainer appContainer;
    
    public BaseController() {
        this.appContainer = DefaultAppContainer.getInstance();
    }
    
    protected AppContainer getAppContainer() {
        return appContainer;
    }
}
