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
        Set<TimeRange> timesNotPossible = new HashSet<TimeRange>();
        for (Event event : events) {
            for (String attendee : attendees) {
                Set<String> eventAttendees = event.getAttendees();
                TimeRange time = event.getWhen();
                if (eventAttendees.contains(attendee)) {
                    timesNotPossible.add(time);
                }
            }
        } 
        List<TimeRange> timesPossible = new ArrayList<TimeRange>();
        List<TimeRange> timesNotPossibleList = new ArrayList<TimeRange>(timesNotPossible);
        Collections.sort(timesNotPossibleList, TimeRange.ORDER_BY_START);  
        int previousEndTime = TimeRange.START_OF_DAY;
        // find possible times
        for (TimeRange time : timesNotPossibleList) {
            if (previousEndTime < time.start()) {
                TimeRange possibleTimeSlot = TimeRange.fromStartEnd(previousEndTime, time.start(), false);
                if (possibleTimeSlot.duration() >= duration) {
                    timesPossible.add(possibleTimeSlot);
                }
            }
            if (previousEndTime < time.end()) {
                previousEndTime = time.end();
            }
        }
        // add last time slot of the day
        if (previousEndTime < TimeRange.END_OF_DAY) {
            TimeRange lastPossibleTimeSlot = TimeRange.fromStartEnd(previousEndTime, TimeRange.END_OF_DAY, true);
            if (lastPossibleTimeSlot.duration() >= duration) {
                timesPossible.add(lastPossibleTimeSlot);
            }
        }
    Collections.sort(timesPossible, TimeRange.ORDER_BY_START);  
    return timesPossible;
  }
}
