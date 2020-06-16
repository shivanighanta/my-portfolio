// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
      Collection<String> attendees = request.getAttendees();
      int duration = (int) request.getDuration();

      // if there are no attendees return whole day
      if (attendees.isEmpty()) {
          return Arrays.asList(TimeRange.WHOLE_DAY);
      }

      // if duration is too long return no options
      if (duration > TimeRange.WHOLE_DAY.duration()) {
          return Arrays.asList();
      }

      // find all the unique conflicting time ranges 
      // go through all the events
        // add each event "when" to conflictingTimes list
        Set<TimeRange> conflictTimes = new HashSet<TimeRange>();
        
        for (Event event : Events) {
            if (attendees.contains(event.getAttendees())) {

            }
        } 






      return Arrays.asList(TimeRange.WHOLE_DAY);
  }
}
