import { useEffect, useState } from "react";

export default function useIntervalFetchState(initial, url, jwt, setMessage, setVisible, id = null, delay = 1000) {
    const [data, setData] = useState(initial);

    useEffect(() => {
        if (!url) return;

        let ignore = false;

        const doFetch = async () => {
            if (ignore) return;
            if (id && id === "new") return;

            try {
                const resp = await fetch(url, jwt ? {
                    headers: {
                        "Authorization": `Bearer ${jwt}`,
                    },
                } : {});

                if (ignore) return;

                if (resp.status === 204) {
                    return;
                }
                if (resp.status === 404) {
                    if (setMessage !== null) {
                        setMessage('Game not found or deleted');
                        setVisible(true);
                    }
                    throw new Error('stop-polling-not-found');
                }

                const contentType = resp.headers.get('content-type') || '';
                const isJson = contentType.toLowerCase().includes('application/json');
                const json = isJson ? await resp.json() : null;

                if (!resp.ok) {
                    if (!ignore) {
                        if (json?.message) {
                            if (setMessage !== null) {
                                setMessage(json.message);
                                setVisible(true);
                            } else {
                                window.alert(json.message);
                            }
                        } else {
                            if (setMessage !== null) {
                                setMessage(`Error ${resp.status}`);
                                setVisible(true);
                            }
                        }
                    }
                    throw new Error('fetch-stopped-due-to-error');
                }

                if (!ignore) {
                    if (json && json.message) {
                        if (setMessage !== null) {
                            setMessage(json.message);
                            setVisible(true);
                        } else {
                            window.alert(json.message);
                        }
                        throw new Error('stop-polling-message');
                    } else {
                        setData(json ?? initial);
                    }
                }
            } catch (err) {
                if (err.message === 'stop-polling-not-found' || err.message === 'stop-polling-message' || err.message === 'fetch-stopped-due-to-error') {
                    return;
                }
                if (!ignore) {
                    console.error('Error fetching data:', err);
                    if (setMessage) {
                        setMessage('Failed to fetch data');
                        setVisible(true);
                    }
                }
            }
        };

        doFetch();
        const intervalId = setInterval(doFetch, delay);

        return () => {
            ignore = true;
            clearInterval(intervalId);
        };
    }, [url, id, jwt, setMessage, setVisible, delay, initial]);

    return [data, setData];
}