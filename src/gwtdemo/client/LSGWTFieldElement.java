/*
 * Copyright 2014 Weswit Srl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gwtdemo.client;

/**
 * Lightstreamer GWT Client support class for describing
 * Lightstreamer Schema elements properties, such as
 * schema id and its mnemonic name (for the UI).
 *
 */
final class LSGWTFieldElement {

	private String id;
	private String name;

	LSGWTFieldElement(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Return Schema element mnemonic name.
	 * @return element mnemonic name
	 */
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.id;
	}

}
