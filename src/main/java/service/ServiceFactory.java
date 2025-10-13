package service;

import service.auth.AuthService;
import service.auth.AuthServiceDaoImpl;
import service.auth.AuthServiceImpl;
import service.auth.FallbackAuthService;

/**
 * Simple factory to obtain service instances. Replace with DI later if needed.
 */
public final class ServiceFactory {
    // Add other service singletons here when their interfaces/impls exist
    private static AuthService authService;
    private static service.register.RegisterService registerService;

    public static AuthService getAuthService(String baseUrl) {
        if (authService == null) {
            // try create HTTP-backed service; if anything fails, fall back to DAO-backed service
            AuthService daoService = new AuthServiceDaoImpl();
            try {
                API.account.AccountApi api = new API.account.HttpAccountApi(baseUrl);
                AuthService httpService = new AuthServiceImpl(api);
                // prefer http but fallback to dao on exception
                authService = new FallbackAuthService(httpService, daoService);
            } catch (Throwable t) {
                // could not construct HTTP client - use DAO directly
                authService = daoService;
            }
        }
        return authService;
    }
    public static service.register.RegisterService getRegisterService(String baseUrl) {
        if (registerService == null) {
            service.register.RegisterService daoService = new service.register.RegisterServiceDaoImpl();
            try {
                API.account.AccountApi accountApi = new API.account.HttpAccountApi(baseUrl);
                API.user.UserApi userApi = new API.user.HttpUserApi(baseUrl);
                API.manager.ManagerApi managerApi = new API.manager.HttpManagerApi(baseUrl);
                service.register.RegisterService httpService = new service.register.RegisterServiceImpl(accountApi, userApi, managerApi);
                registerService = new service.register.FallbackRegisterService(httpService, daoService);
            } catch (Throwable t) {
                registerService = daoService;
            }
        }
        return registerService;
    }
}
