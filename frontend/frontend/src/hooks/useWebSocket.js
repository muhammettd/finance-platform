import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { addTrade } from '../store/tradeSlice';
import { setOrderBook } from '../store/orderBookSlice';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const useWebSocket = (symbolPair = 'BTC_USDT') => {
    const dispatch = useDispatch();

    useEffect(() => {
        const stompClient = new Client({
            webSocketFactory: () => new SockJS('http://localhost:8080/ws-endpoint'),


            onConnect: () => {
                console.log('STOMP Bağlantısı Kuruldu! (' + symbolPair + ')');

                // 1. TRADES HISTORY SUBCRIBE
                stompClient.subscribe(`/topic/trades`, (message) => {
                    if (message.body) {
                        try {
                            const tradeData = JSON.parse(message.body);
                            console.log('YENİ İŞLEM GELDİ:', tradeData);
                            dispatch(addTrade(tradeData));
                        } catch (error) {
                            console.error('Trade parse hatası:', error);
                        }
                    }
                });

                // ORDER BOOK SUBCRIBE
                stompClient.subscribe(`/topic/orderbook/${symbolPair}`, (message) => {

                    console.log("BACKEND'DEN YENİ TAHTA GELDİ!!!", message.body);

                    if (message.body) {
                        try {
                            const orderBookData = JSON.parse(message.body);


                            console.log("REDUX'A GİDECEK VERİ:", orderBookData);

                            dispatch(setOrderBook(orderBookData));
                        } catch (error) {
                            console.error('OrderBook parse hatası:', error);
                        }
                    }
                });
            },

            onStompError: (frame) => {
                console.error('STOMP Hatası:', frame.headers['message']);
            },
            onWebSocketError: (event) => {
                console.error('WebSocket Altyapı Hatası!', event);
            }
        });

        stompClient.activate();

        return () => {
            stompClient.deactivate();
            console.log('STOMP Bağlantısı Kapatıldı.');
        };
    }, [dispatch, symbolPair]);
};

export default useWebSocket;