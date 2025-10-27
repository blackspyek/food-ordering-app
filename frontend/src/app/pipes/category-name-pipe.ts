import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
	name: "categoryName",
})
export class CategoryNamePipe implements PipeTransform {
	transform(value: string): string {
		return value.includes("_") ? value.split("_")[0] : value;
	}
}
