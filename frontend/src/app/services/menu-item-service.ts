import { inject, Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../environments/environment";
import { Category, MenuItem } from "../global.types";
import { Observable } from "rxjs";

@Injectable({
	providedIn: "root",
})
export class MenuItemService {
	private http = inject(HttpClient);
	private api = environment.apiUrl + "menu";

	getMenuItemsByCategory(category: Category): Observable<MenuItem[]> {
		return this.http.get<MenuItem[]>(`${this.api}/category/${category}`);
	}
	getMenuItemById(id: number): Observable<MenuItem> {
		return this.http.get<MenuItem>(`${this.api}/${id}`);
	}
	getMenuItems(): Observable<MenuItem[]> {
		return this.http.get<MenuItem[]>(`${this.api}`);
	}
}
