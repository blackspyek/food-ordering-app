import { OrderBoardState } from "../global.types";
export function parseJavaMapToOrderBoard(text: string): OrderBoardState {
	console.log("RAW WS BODY:", text);
	const result: OrderBoardState = {
		liveOrderBoardCodes: [],
		liveOrderBoardReadyCodes: [],
	};

	if (!text) return result;

	try {
		const json = JSON.parse(text);
		// Sprawdzamy, czy json ma oczekiwane pola, jeśli tak - zwracamy
		if (json && (json.liveOrderBoardCodes || json.liveOrderBoardReadyCodes)) {
			return {
				liveOrderBoardCodes: json.liveOrderBoardCodes || [],
				liveOrderBoardReadyCodes: json.liveOrderBoardReadyCodes || [], // Tutaj mapujemy klucz z Mapy Springa
			};
		}
	} catch (e) {}

	const content = text.trim().replace(/^\{|\}$/g, "");
	const regex = /([a-zA-Z0-9]+)=\[([^\]]*)\]/g;

	const matches = content.matchAll(regex);

	for (const match of matches) {
		const key = match[1];
		const valuesStr = match[2];

		const valuesArray = valuesStr.trim()
			? valuesStr.split(",").map((s) => s.trim())
			: [];

		if (key === "liveOrderBoardCodes") {
			result.liveOrderBoardCodes = valuesArray;
		} else if (key === "liveOrderBoardReadyCodes") {
			result.liveOrderBoardReadyCodes = valuesArray;
		}
	}

	return result;
}
