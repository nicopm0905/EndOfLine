import tokenService from './token.service';

const setupFetchInterceptor = () => {
    const originalFetch = window.fetch;

    window.fetch = async (...args) => {
        try {
            const response = await originalFetch(...args);

            if (response.status === 401 || response.status === 403) {
                const url = args[0];
                
                if (!url.includes('/api/v1/auth/signin') && 
                    !url.includes('/api/v1/auth/signup') &&
                    !window.location.pathname.includes('/login') &&
                    !window.location.pathname.includes('/register')) {
                    
                    console.error('Authentication failed. Redirecting to login...');
                    tokenService.removeUser();
                    window.location.href = '/login';
                }
            }

            return response;
        } catch (error) {
            console.error('Fetch error:', error);
            throw error;
        }
    };
};

export default setupFetchInterceptor;
