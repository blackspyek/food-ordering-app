import { Component, inject, input } from "@angular/core";
import {
	CurrencyPipe,
	DatePipe,
	DecimalPipe,
	LowerCasePipe,
	TitleCasePipe,
} from "@angular/common";
import { OrderService } from "../../services/order-service";
import { toObservable, toSignal } from "@angular/core/rxjs-interop";
import { map, switchMap, catchError, of, startWith } from "rxjs"; // Dodane importy
import { HttpErrorResponse } from "@angular/common/http";
import { StatusNamePipe } from "../../pipes/status-name-pipe";

@Component({
	selector: "app-order-details",
	standalone: true,
	imports: [DatePipe, CurrencyPipe, StatusNamePipe, TitleCasePipe],
	templateUrl: "./order-details.html",
	styleUrl: "./order-details.scss",
	host: { class: "w-full" },
})
export class OrderDetails {
	private orderService = inject(OrderService);

	orderId = input.required<string>();

	orderState = toSignal(
		toObservable(this.orderId).pipe(
			switchMap((id) =>
				this.orderService.getOrderById(id).pipe(
					map((response) => ({
						data: response.data,
						error: null,
						loading: false,
					})),

					catchError((err: HttpErrorResponse) => {
						const errorMessage =
							err.status === 404
								? "Nie znaleziono zamówienia."
								: "Wystąpił nieoczekiwany błąd.";

						return of({
							data: null,
							error: errorMessage,
							loading: false,
						});
					}),

					startWith({ data: null, error: null, loading: true }),
				),
			),
		),
	);
}
