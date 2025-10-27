import { Injectable, OnDestroy, signal } from "@angular/core";
import { OrderBoardState } from "../global.types";
import { Client } from "@stomp/stompjs";
import { environment } from "../../environments/environment";
import { parseJavaMapToOrderBoard } from "../utils/java-map-parser";

@Injectable({
	providedIn: "root",
})
export class OrderSocketService implements OnDestroy {
	readonly boardState = signal<OrderBoardState>({
		liveOrderBoardCodes: [],
		liveOrderBoardReadyCodes: [],
	});
	private client: Client;
	constructor() {
		this.client = new Client({
			brokerURL: environment.wsBrokerUrl,
			reconnectDelay: 5000,
			onConnect: () => {
				this.client.subscribe("/topic/orderBoard", (message) => {
					if (message.body) {
						try {
							const data: OrderBoardState = parseJavaMapToOrderBoard(
								message.body,
							);
							console.log("STOMP: Received dataasdasdsad", message.body);
							this.boardState.set(data);
						} catch (e) {
							console.error("STOMP: JSON Parse error", e);
						}
					}
				});
				this.client.publish({
					destination: "/app/sendMessage",
					body: "Init State Request",
				});
			},
			onStompError: (frame) => {
				console.error(
					"STOMP: Broker reported error: " + frame.headers["message"],
				);
			},
			onWebSocketClose: (event) => {
				console.warn("STOMP: WebSocket closed", event);
			},
		});
		this.client.activate();
	}
	ngOnDestroy() {
		this.client.deactivate();
	}
}
