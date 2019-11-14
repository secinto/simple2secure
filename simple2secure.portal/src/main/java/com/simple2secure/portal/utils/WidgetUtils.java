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
package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple2secure.api.dto.WidgetDTO;
import com.simple2secure.api.model.Widget;
import com.simple2secure.api.model.WidgetProperties;
import com.simple2secure.api.model.WidgetUserRelation;
import com.simple2secure.portal.repository.WidgetPropertiesRepository;
import com.simple2secure.portal.repository.WidgetRepository;
import com.simple2secure.portal.repository.WidgetUserRelRepository;

@Component
public class WidgetUtils {

	private static Logger log = LoggerFactory.getLogger(WidgetUtils.class);

	@Autowired
	WidgetUserRelRepository widgetUserRelRepository;

	@Autowired
	WidgetPropertiesRepository widgetPropertiesRepository;

	@Autowired
	WidgetRepository widgetRepository;

	public List<WidgetDTO> getWidgetsByUserAndContextId(String userId, String contextId) {
		List<WidgetDTO> widgetDTOList = new ArrayList<>();
		List<WidgetUserRelation> relations = widgetUserRelRepository.getPropertiesByUserIdAndContextId(userId, contextId);
		if (relations != null) {
			for (WidgetUserRelation relation : relations) {
				if (relation != null) {
					WidgetProperties properties = widgetPropertiesRepository.find(relation.getWidgetPropertiesId());
					Widget widget = widgetRepository.find(relation.getWidgetId());
					widgetDTOList.add(new WidgetDTO(widget, properties));
				}
			}
		}
		return widgetDTOList;
	}

}
