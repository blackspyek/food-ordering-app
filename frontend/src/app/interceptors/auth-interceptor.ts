import { HttpInterceptorFn } from "@angular/common/http";
import { environment } from "../../environments/environment";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
	if (req.url.startsWith(environment.apiUrl)) {
		const modifiedReq = req.clone({
			setHeaders: {
				Authorization: `Bearer ${localStorage.getItem("authToken") || ""}`,
			},
		});
		return next(modifiedReq);
	}
	return next(req);
};
