import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
	name: "statusName",
})
export class StatusNamePipe implements PipeTransform {
	transform(value: string): string {
		return value.toUpperCase().replace(/_/g, " ");
	}
}
