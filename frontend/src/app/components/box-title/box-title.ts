import { Component, input, InputSignal } from "@angular/core";
import { UpperCasePipe } from "@angular/common";

@Component({
	selector: "app-box-title",
	imports: [UpperCasePipe],
	templateUrl: "./box-title.html",
	styleUrl: "./box-title.scss",
	host: {
		class: "w-full max-w-xs",
	},
})
export class BoxTitle {
	title: InputSignal<string> = input.required<string>();
	state: InputSignal<"primary" | "secondary"> = input<"primary" | "secondary">(
		"secondary",
	);
}
