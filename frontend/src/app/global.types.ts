export type DeliveryMethod = "DINE_IN" | "TAKE_OUT" | "";

export enum Category {
	RAMEN = "RAMEN",
	RICE_NOODLES = "RICE_NOODLES",
	UDON_NOODLES = "UDON_NOODLES",
}
export const categoryNames: Category[] = [
	Category.RAMEN,
	Category.RICE_NOODLES,
	Category.UDON_NOODLES,
];

export interface MenuItem {
	id: number;
	name: string;
	description?: string;
	price: number;
	available: boolean;
	category: Category;
	photoUrl?: string;
	bestSeller?: boolean;
}
export type OrderStatus =
	| "IN_PREPARATION"
	| "READY_FOR_PICKUP"
	| "PICKED_UP"
	| "CANCELLED"
	| "DIDNT_PICK_UP";

export interface Order {
	status: OrderStatus;
	orderType: DeliveryMethod;
	orderId: string;
	totalPrice: number;
	orderTime: string;
	orderItems: OrderedItemRecord[];
	email?: string;
	boardCode: number;
}
export interface OrderedItemRecord {
	item: MenuItem;
	quantity: number;
	totalPrice: number;
	orderItemId?: number;
}

export interface CreateOrderItemDto {
	menuItemId: number;
	quantity: number;
}
export interface CreateOrderDto {
	email: string;
	name: string;
	orderType: DeliveryMethod;
	orderItems: CreateOrderItemDto[];
	userId: number | null;
}
export interface OrderResponse {
	data: Order;
	message: string;
	status: number;
}
export interface ApiResponse<T> {
	data: T;
	message: string;
	status: number;
}

export interface OrderBoardState {
	liveOrderBoardCodes: string[]; // Secondary codes that are live
	liveOrderBoardReadyCodes: string[]; // Primary codes that are ready
}
export default Order;
