/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */

import {Directive, forwardRef, Attribute} from '@angular/core';
import {Validator, AbstractControl, NG_VALIDATORS} from '@angular/forms';

@Directive({
	selector: '[validateEqual][formControlName],[validateEqual][formControl],[validateEqual][ngModel]',
	providers: [
		{provide: NG_VALIDATORS, useExisting: forwardRef(() => EqualValidator), multi: true}
	]
})
export class EqualValidator implements Validator {
	constructor(@Attribute('validateEqual') public validateEqual: string,
	            @Attribute('reverse') public reverse: string)
	{

	}

	private get isReverse() {
		if (!this.reverse) return false;
		return this.reverse === 'true' ? true : false;
	}

	validate(c: AbstractControl): { [key: string]: any } {
		// self value
		const v = c.value;

		// control vlaue
		const e = c.root.get(this.validateEqual);

		// value not equal
		if (e && v !== e.value && !this.isReverse) {
			return {
				validateEqual: false
			};
		}

		// value equal and reverse
		if (e && v === e.value && this.isReverse) {
			delete e.errors['validateEqual'];
			if (!Object.keys(e.errors).length) e.setErrors(null);
		}

		// value not equal and reverse
		if (e && v !== e.value && this.isReverse) {
			e.setErrors({
				validateEqual: false
			});
		}

		return null;
	}
}
